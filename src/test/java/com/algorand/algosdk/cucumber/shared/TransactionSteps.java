package com.algorand.algosdk.cucumber.shared;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.algod.client.model.TransactionParams;
import com.algorand.algosdk.builder.transaction.ApplicationBaseTransactionBuilder;
import com.algorand.algosdk.builder.transaction.TransactionBuilder;
import com.algorand.algosdk.crypto.*;
import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.unit.Base;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.util.ResourceUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import static com.algorand.algosdk.util.ConversionUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TransactionSteps {
    private final Base base;
    public Transaction applicationTransaction = null;
    public SignedTransaction signedTransaction = null;

    public TransactionSteps(Base b) {
        this.base = b;
    }

    public static TEALProgram loadTEALProgramFromFile(String file) {
        return new TEALProgram(loadResource(file));
    }

    public static byte[] loadResource(String file) {
        try {
            return ResourceUtils.readResource(file);
        } catch (Exception e) {
            Assertions.fail("Unable to read file ('"+file+"') required by test: " + e.getMessage(), e);
        }

        throw new RuntimeException("Unknown error.");
    }

    @When("I build an application transaction with operation {string}, application-id {long}, sender {string}, approval-program {string}, clear-program {string}, global-bytes {long}, global-ints {long}, local-bytes {long}, local-ints {long}, app-args {string}, foreign-apps {string}, foreign-assets {string}, app-accounts {string}, fee {long}, first-valid {long}, last-valid {long}, genesis-hash {string}, extra-pages {long}")
    public void buildApplicationTransactions(String operation, Long applicationId, String sender, String approvalProgramFile, String clearProgramFile, Long globalBytes, Long globalInts, Long localBytes, Long localInts, String appArgs, String foreignApps, String foreignAssets, String appAccounts, Long fee, Long firstValid, Long lastValid, String genesisHash, Long extraPages) {
        ApplicationBaseTransactionBuilder builder = null;

        // Create builder and apply builder-specific parameters
        switch (operation) {
            case "create":
                builder = Transaction.ApplicationCreateTransactionBuilder()
                        .approvalProgram(loadTEALProgramFromFile(approvalProgramFile))
                        .clearStateProgram(loadTEALProgramFromFile(clearProgramFile))
                        .globalStateSchema(new StateSchema(globalInts, globalBytes))
                        .localStateSchema(new StateSchema(localInts, localBytes))
                        .extraPages(extraPages);
                break;
            case "update":
                builder = Transaction.ApplicationUpdateTransactionBuilder()
                        .approvalProgram(loadTEALProgramFromFile(approvalProgramFile))
                        .clearStateProgram(loadTEALProgramFromFile(clearProgramFile));
                break;
            case "call":
                builder = Transaction.ApplicationCallTransactionBuilder();
                break;
            case "optin":
                builder = Transaction.ApplicationOptInTransactionBuilder();
                break;
            case "clear":
                builder = Transaction.ApplicationClearTransactionBuilder();
                break;
            case "closeout":
                builder = Transaction.ApplicationCloseTransactionBuilder();
                break;
            case "delete":
                builder = Transaction.ApplicationDeleteTransactionBuilder();
                break;
            default:
                Assertions.fail("Need an option to build: " + operation);

        }

        builder.sender(sender);
        builder.flatFee(fee);


        // Shared base fields
        if (applicationId != 0) {
            builder.applicationId(applicationId);
        }
        if (StringUtils.isNotEmpty(appArgs)) {
            builder.args(convertArgs(appArgs));
        }
        if (StringUtils.isNotEmpty(foreignApps)) {
            builder.foreignApps(convertForeignApps(foreignApps));
        }
        if (StringUtils.isNotEmpty(foreignAssets)) {
            builder.foreignAssets(convertForeignApps(foreignAssets));
        }
        if (StringUtils.isNotEmpty(appAccounts)) {
            builder.accounts(convertAccounts(appAccounts));
        }

        if (firstValid != 0) {
            builder.firstValid(firstValid);
        }
        if (lastValid != 0) {
            builder.lastValid(lastValid);
        }
        if (StringUtils.isNotEmpty(genesisHash)) {
            builder.genesisHashB64(genesisHash);
        }

        applicationTransaction = builder.build();
    }

    @When("sign the transaction")
    public void sign_the_transaction() throws NoSuchAlgorithmException {
        signedTransaction = base.signTransaction(applicationTransaction);
    }

    @Then("fee field is in txn")
    public void fee_field_is_in_txn() throws IOException {
        if (signedTransaction == null) return;

        byte[] encodedTxn = Encoder.encodeToMsgPack(signedTransaction);
        Map<String,Object> sigtxn = Encoder.decodeFromMsgPack(encodedTxn,Map.class);
        assertThat(sigtxn).isNotNull();
        Map<String,Object> txn = (Map<String,Object>) sigtxn.get("txn");
        assertThat(txn).containsKey("fee");
    }

    @Then("fee field not in txn")
    public void  fee_field_not_in_txn() throws IOException {
        if (signedTransaction == null) return;

        byte[] encodedTxn = Encoder.encodeToMsgPack(signedTransaction);
        Map<String,Object> sigtxn = Encoder.decodeFromMsgPack(encodedTxn,Map.class);
        assertThat(sigtxn).isNotNull();
        Map<String,Object> txn = (Map<String,Object>) sigtxn.get("txn");
        assertThat(txn).doesNotContainKey("fee");
    }
    @Then("the base64 encoded signed transaction should equal {string}")
    public void the_base64_encoded_signed_transaction_should_equal(String golden) throws JsonProcessingException {
        String encoded = Encoder.encodeToBase64(Encoder.encodeToMsgPack(signedTransaction));
        assertThat(encoded).isEqualTo(golden);
    }

}
