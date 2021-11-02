package com.algorand.algosdk.cucumber.shared;

import com.algorand.algosdk.builder.transaction.ApplicationBaseTransactionBuilder;
import com.algorand.algosdk.builder.transaction.KeyRegistrationTransactionBuilder;
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
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import static com.algorand.algosdk.util.ConversionUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TransactionSteps {
    private final Base base;
    
    // The Java SDK does not have a comprehensive suggested params object, so all fields are defined here individually
    public Integer suggestedFee;
    public Boolean suggestedFlatFee;
    public Integer suggestedFirstValid;
    public Integer suggestedLastValid;
    public String suggestedGenesisHashB64;
    public String suggestedGenesisId;

    public Transaction transaction = null;
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

    @Given("suggested transaction parameters fee {int}, flat-fee {string}, first-valid {int}, last-valid {int}, genesis-hash {string}, genesis-id {string}")
    public void suggested_transaction_parameters(Integer fee, String flatFee, Integer firstValid, Integer lastValid, String genesisHash, String genesisId) {
        assertThat(flatFee).isIn(List.of("true", "false"));
        
        suggestedFee = fee;
        suggestedFlatFee = flatFee.equals("true");
        suggestedFirstValid = firstValid;
        suggestedLastValid = lastValid;
        suggestedGenesisHashB64 = genesisHash;
        suggestedGenesisId = genesisId;
    }

    @When("I build a keyreg transaction with sender {string}, nonparticipation {string}, vote first {int}, vote last {int}, key dilution {int}, vote public key {string}, selection public key {string}, and state proof public key {string}")
    public void buildKeyregTransaction(String sender, String nonpart, Integer voteFirst, Integer voteLast, Integer keyDilution, String votePk, String selectionPk, String stateProofPk) {
        assertThat(nonpart).isIn(List.of("true", "false"));

        KeyRegistrationTransactionBuilder<?> builder = Transaction.KeyRegistrationTransactionBuilder();

        if (suggestedFlatFee) {
            builder = builder.flatFee(suggestedFee);
        } else {
            builder = builder.fee(suggestedFee);
        }
        
        transaction = builder
            .firstValid(suggestedFirstValid)
            .lastValid(suggestedLastValid)
            .genesisHashB64(suggestedGenesisHashB64)
            .genesisID(suggestedGenesisId)
            .sender(sender)
            .nonparticipation(nonpart.equals("true"))
            .voteFirst(voteFirst)
            .voteLast(voteLast)
            .voteKeyDilution(keyDilution)
            .participationPublicKeyBase64(votePk)
            .selectionPublicKeyBase64(selectionPk)
            // TODO: .stateProofKeyBase64(stateProofPk)
            .build();
    }

    @When("I build an application transaction with operation {string}, application-id {long}, sender {string}, approval-program {string}, clear-program {string}, global-bytes {long}, global-ints {long}, local-bytes {long}, local-ints {long}, app-args {string}, foreign-apps {string}, foreign-assets {string}, app-accounts {string}, fee {long}, first-valid {long}, last-valid {long}, genesis-hash {string}, extra-pages {long}")
    public void buildApplicationTransactions(String operation, Long applicationId, String sender, String approvalProgramFile, String clearProgramFile, Long globalBytes, Long globalInts, Long localBytes, Long localInts, String appArgs, String foreignApps, String foreignAssets, String appAccounts, Long fee, Long firstValid, Long lastValid, String genesisHash, Long extraPages) {
        ApplicationBaseTransactionBuilder<?> builder = null;

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

        transaction = builder.build();
    }

    @When("sign the transaction")
    public void sign_the_transaction() throws NoSuchAlgorithmException {
        signedTransaction = base.signTransaction(transaction);
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
        assertThat(decoded.tx).isEqualTo(transaction);
    }

}
