package com.algorand.algosdk.unit;

import static org.assertj.core.api.Assertions.assertThat;

import com.algorand.algosdk.abi.ABIType;
import com.algorand.algosdk.abi.Method;
import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.cucumber.shared.TransactionSteps;
import com.algorand.algosdk.transaction.AtomicTransactionComposer;
import com.algorand.algosdk.transaction.MethodCalParams;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class AtomicTxnComposer {
    public static String token = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    public static Integer algodPort = 60000;

    AlgodClient aclv2;

    Base base;
    ABIJson methodABI;
    AtomicTransactionComposer atc;
    Integer appID;

    TransactionSteps transSteps;
    Account signingAccount;
    AtomicTransactionComposer.TxnSigner txnSigner;
    List<SignedTransaction> signedTxnsGathered;

    AtomicTransactionComposer.TransactionWithSigner txnWithSigner;
    MethodCalParams.Builder optionBuilder;

    public AtomicTxnComposer(Base b, ABIJson methodABI_, TransactionSteps steps) {
        base = b;
        methodABI = methodABI_;
        transSteps = steps;
    }

    @Given("an algod v2 client")
    public void an_algod_v2_client() {
        aclv2 = new com.algorand.algosdk.v2.client.common.AlgodClient(
                "http://localhost", algodPort, token
        );
    }

    @Given("a new AtomicTransactionComposer")
    public void a_new_atomic_transaction_composer() {
        this.atc = new AtomicTransactionComposer();
    }

    @Given("an application id {int}")
    public void an_application_id(Integer int1) {
        this.appID = int1;
    }

    @When("I make a transaction signer for the signing account.")
    public void i_make_a_transaction_signer_for_the_signing_account() {
        signingAccount = base.signingAccounts.values().iterator().next();
        txnSigner = signingAccount.getTransactionSigner();
    }

    @When("I create a transaction with signer with the current transaction.")
    public void i_create_a_transaction_with_signer_with_the_current_transaction() {
        this.txnWithSigner = new AtomicTransactionComposer.TransactionWithSigner(this.transSteps.builtTransaction, this.txnSigner);
    }

    @When("I create a new method arguments array.")
    public void i_create_a_new_method_arguments_array() {
        this.optionBuilder = new MethodCalParams.Builder();
    }

    @When("I append the current transaction with signer to the method arguments array.")
    public void i_append_the_current_transaction_with_signer_to_the_method_arguments_array() {
        this.optionBuilder.addMethodArgs(this.txnWithSigner);
    }

    @When("I add the current transaction with signer to the composer.")
    public void i_add_the_current_transaction_with_signer_to_the_composer() {
        this.atc.addTransaction(this.txnWithSigner);
    }

    private List<AtomicTransactionComposer.ABIValue> splitAndProcessABIArgs(String str) {
        String[] argTokens = str.split(",");
        List<AtomicTransactionComposer.ABIValue> res = new ArrayList<>();

        int argTokenIndex = 0;
        for (Method.Arg argType : methodABI.method.args) {
            if (Method.TxArgTypes.contains(argType.type))
                continue;
            ABIType abiT = ABIType.valueOf(argType.type);
            byte[] abiEncoded = Encoder.decodeFromBase64(argTokens[argTokenIndex]);
            Object decodedValue = abiT.decode(abiEncoded);
            argTokenIndex++;
            res.add(new AtomicTransactionComposer.ABIValue(abiT, decodedValue));
        }

        return res;
    }

    @When("I append the encoded arguments {string} to the method arguments array.")
    public void i_append_the_encoded_arguments_to_the_method_arguments_array(String string) {
        List<AtomicTransactionComposer.ABIValue> processedABIArgs = splitAndProcessABIArgs(string);
        for (AtomicTransactionComposer.ABIValue arg : processedABIArgs)
            this.optionBuilder.addMethodArgs(arg);
    }

    @When("I add a method call with the signing account, the current application, suggested params, on complete {string}, current transaction signer, current method arguments.")
    public void i_add_a_method_call_with_the_signing_account_the_current_application_suggested_params_on_complete_current_transaction_signer_current_method_arguments(String string) {
        optionBuilder
                .setOnComplete(Transaction.OnCompletion.String(string))
                .setSender(signingAccount.getAddress().toString())
                .setSigner(txnSigner)
                .setAppID(appID.longValue())
                .setMethod(methodABI.method)
                .setSuggestedParams(this.transSteps.suggestedParams)
                .setFirstValid(this.transSteps.fv)
                .setLastValid(this.transSteps.lv)
                .setFee(this.transSteps.fee)
                .setFlatFee(this.transSteps.flatFee);
        MethodCalParams optionBuild = optionBuilder.build();
        atc.addMethodCall(optionBuild);
    }

    @When("I build the transaction group with the composer. If there is an error it is {string}.")
    public void i_build_the_transaction_group_with_the_composer_if_there_is_an_error_it_is(String string) {
        String errStr = "";
        try {
            atc.buildGroup();
        } catch (Exception e) {
            errStr = e.getMessage();
        }
        assertThat(errStr).isEqualTo(string);
    }

    @Then("The composer should have a status of {string}.")
    public void the_composer_should_have_a_status_of(String string) {
        assertThat(atc.getStatus()).isEqualTo(AtomicTransactionComposer.Status.valueOf(string));
    }

    @Then("I gather signatures with the composer.")
    public void i_gather_signatures_with_the_composer() throws IOException, NoSuchAlgorithmException {
        signedTxnsGathered = atc.gatherSignatures();
    }

    @Then("the base64 encoded signed transactions should equal {string}")
    public void the_base64_encoded_signed_transactions_should_equal(String string) throws IOException {
        String[] splitStr = string.split(",");
        for (int i = 0; i < splitStr.length; i++) {
            String subStr = splitStr[i];
            SignedTransaction decodedMsgPack = Encoder.decodeFromMsgPack(subStr, SignedTransaction.class);
            SignedTransaction actual = signedTxnsGathered.get(i);
            assertThat(actual).isEqualTo(decodedMsgPack);
        }
    }
}
