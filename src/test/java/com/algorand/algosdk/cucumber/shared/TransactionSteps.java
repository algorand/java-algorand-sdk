package com.algorand.algosdk.cucumber.shared;

import static com.algorand.algosdk.util.ConversionUtils.convertAccounts;
import static com.algorand.algosdk.util.ConversionUtils.convertArgs;
import static com.algorand.algosdk.util.ConversionUtils.convertForeignApps;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import com.algorand.algosdk.builder.transaction.ApplicationBaseTransactionBuilder;
import com.algorand.algosdk.builder.transaction.KeyRegistrationTransactionBuilder;
import com.algorand.algosdk.builder.transaction.PaymentTransactionBuilder;
import com.algorand.algosdk.integration.TransientAccount;
import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.unit.Base;
import com.algorand.algosdk.util.Encoder;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


import static com.algorand.algosdk.util.ConversionUtils.*;
import static com.algorand.algosdk.util.ResourceUtils.loadTEALProgramFromFile;

public class TransactionSteps {
    public Base base;
    public Transaction builtTransaction = null;
    public SignedTransaction signedTransaction = null;
    public TransientAccount transAcc;

    public BigInteger fee, flatFee, fv, lv;
    public byte[] genesisHash;
    public String genesisID;

    public TransactionSteps(Base b) {
        this.base = b;
    }

    @When("I build an application transaction with operation {string}, application-id {long}, sender {string}, approval-program {string}, clear-program {string}, global-bytes {long}, global-ints {long}, local-bytes {long}, local-ints {long}, app-args {string}, foreign-apps {string}, foreign-assets {string}, app-accounts {string}, fee {long}, first-valid {long}, last-valid {long}, genesis-hash {string}, extra-pages {long}, boxes {string}")
    public void buildApplicationTransactions(String operation, Long applicationId, String sender, String approvalProgramFile, String clearProgramFile, Long globalBytes, Long globalInts, Long localBytes, Long localInts, String appArgs, String foreignApps, String foreignAssets, String appAccounts, Long fee, Long firstValid, Long lastValid, String genesisHash, Long extraPages, String boxesStr) throws Exception {
        ApplicationBaseTransactionBuilder<?> builder = null;

        // Create builder and apply builder-specific parameters
        switch (operation) {
            case "create":
                builder = Transaction.ApplicationCreateTransactionBuilder()
                        .approvalProgram(loadTEALProgramFromFile(approvalProgramFile, null))
                        .clearStateProgram(loadTEALProgramFromFile(clearProgramFile, null))
                        .globalStateSchema(new StateSchema(globalInts, globalBytes))
                        .localStateSchema(new StateSchema(localInts, localBytes))
                        .extraPages(extraPages);
                break;
            case "update":
                builder = Transaction.ApplicationUpdateTransactionBuilder()
                        .approvalProgram(loadTEALProgramFromFile(approvalProgramFile, null))
                        .clearStateProgram(loadTEALProgramFromFile(clearProgramFile, null));
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
        if (StringUtils.isNotEmpty(boxesStr)) {
            builder.boxReferences(convertBoxes(boxesStr));
        }

        builtTransaction = builder.build();
    }

    @When("I build a keyreg transaction with sender {string}, nonparticipation {string}, vote first {int}, vote last {int}, key dilution {int}, vote public key {string}, selection public key {string}, and state proof public key {string}")
    public void buildKeyregTransaction(String sender, String nonpart, Integer voteFirst, Integer voteLast, Integer keyDilution, String votePk, String selectionPk, String stateProofPk) {
        assertThat(nonpart).isIn(Arrays.asList("true", "false"));

        KeyRegistrationTransactionBuilder<?> builder = Transaction.KeyRegistrationTransactionBuilder();

        if (!votePk.isEmpty())
            builder = builder.participationPublicKeyBase64(votePk);

        if (!selectionPk.isEmpty())
            builder = builder.selectionPublicKeyBase64(selectionPk);

        if (!stateProofPk.isEmpty())
            builder = builder.stateProofKeyBase64(stateProofPk);
        
        builtTransaction = builder
            .firstValid(fv)
            .lastValid(lv)
            .genesisHash(genesisHash)
            .genesisID(genesisID)
            .fee(fee)
            .flatFee(flatFee)
            .sender(sender)
            .nonparticipation(nonpart.equals("true"))
            .voteFirst(voteFirst)
            .voteLast(voteLast)
            .voteKeyDilution(keyDilution)
            .build();
    }

    @Given("suggested transaction parameters fee {int}, flat-fee {string}, first-valid {int}, last-valid {int}, genesis-hash {string}, genesis-id {string}")
    public void suggested_transaction_parameters_fee_flat_fee_first_valid_last_valid_genesis_hash_genesis_id(Integer int1, String string, Integer int2, Integer int3, String string2, String string3) {
        boolean isFlatFee = Boolean.parseBoolean(string);
        fee = isFlatFee ? null : BigInteger.valueOf(int1);
        flatFee = isFlatFee ? BigInteger.valueOf(int1) : null;

        genesisHash = Encoder.decodeFromBase64(string2);
        genesisID = string3;
        fv = BigInteger.valueOf(int2);
        lv = BigInteger.valueOf(int3);
    }

    @When("I build a payment transaction with sender {string}, receiver {string}, amount {int}, close remainder to {string}")
    public void i_build_a_payment_transaction_with_sender_receiver_amount_close_remainder_to(String string, String string2, Integer int1, String string3) {
        PaymentTransactionBuilder<?> builder = PaymentTransactionBuilder.Builder();
        if (string.equals("transient"))
            builder.sender(transAcc.transientAccount.getAddress());
        else
            builder.sender(string);
        if (string2.equals("transient"))
            builder.receiver(transAcc.transientAccount.getAddress());
        else
            builder.receiver(string2);
        builder.amount(int1)
                .fee(fee).flatFee(flatFee)
                .firstValid(fv).lastValid(lv)
                .genesisHash(genesisHash).genesisID(genesisID);
        if (!string3.isEmpty())
            builder.closeRemainderTo(string3);
        builtTransaction = builder.build();
    }

    @When("sign the transaction")
    public void sign_the_transaction() throws NoSuchAlgorithmException {
        signedTransaction = base.signTransaction(builtTransaction);
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

    @Then("the decoded transaction should equal the original")
    public void the_decoded_transaction_should_equal_the_original() throws JsonProcessingException, IOException {
        String encoded = Encoder.encodeToBase64(Encoder.encodeToMsgPack(signedTransaction));
        SignedTransaction decoded = Encoder.decodeFromMsgPack(encoded, SignedTransaction.class);
        assertThat(decoded.tx).isEqualTo(builtTransaction);
    }

}
