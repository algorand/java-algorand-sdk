package com.algorand.algosdk.unit;

import static org.assertj.core.api.Assertions.assertThat;

import com.algorand.algosdk.abi.ABIType;
import com.algorand.algosdk.abi.Method;
import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.algod.client.model.TransactionParams;
import com.algorand.algosdk.builder.transaction.PaymentTransactionBuilder;
import com.algorand.algosdk.transaction.AtomicTransactionComposer;
import com.algorand.algosdk.transaction.MethodCallOption;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.math.BigInteger;
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
    TransactionParams suggestedParams;
    BigInteger fee, flatFee, fv, lv;
    boolean isFlatFee;
    byte[] genesisHash;
    String genesisID;
    Account signingAccount;
    AtomicTransactionComposer.TransactionSigner txnSigner;
    List<SignedTransaction> signedTxnsGathered;
    Transaction paymentTxn;
    AtomicTransactionComposer.TransactionWithSigner txnWithSigner;
    MethodCallOption.MethodCallOptionBuilder optionBuilder;

    public AtomicTxnComposer(Base b, ABIJson methodABI_) {
        base = b;
        methodABI = methodABI_;
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

    @Given("suggested transaction parameters fee {int}, flat-fee {string}, first-valid {int}, last-valid {int}, genesis-hash {string}, genesis-id {string}")
    public void suggested_transaction_parameters_fee_flat_fee_first_valid_last_valid_genesis_hash_genesis_id(Integer int1, String string, Integer int2, Integer int3, String string2, String string3) {
        isFlatFee = Boolean.parseBoolean(string);
        fee = isFlatFee ? null : BigInteger.valueOf(int1);
        flatFee = isFlatFee ? BigInteger.valueOf(int1) : null;

        genesisHash = Encoder.decodeFromBase64(string2);
        genesisID = string3;
        fv = BigInteger.valueOf(int2);
        lv = BigInteger.valueOf(int3);

        suggestedParams = new TransactionParams().genesishashb64(genesisHash).genesisID(genesisID).lastRound(fv);
    }

    @Given("an application id {int}")
    public void an_application_id(Integer int1) {
        this.appID = int1;
    }

    @When("I make a transaction signer for the signing account.")
    public void i_make_a_transaction_signer_for_the_signing_account() {
        signingAccount = base.signingAccounts.values().iterator().next();
        txnSigner = new AtomicTransactionComposer.TransactionSigner(signingAccount);
    }

    @When("I build a payment transaction with sender {string}, receiver {string}, amount {int}, close remainder to {string}")
    public void i_build_a_payment_transaction_with_sender_receiver_amount_close_remainder_to(String string, String string2, Integer int1, String string3) {
        PaymentTransactionBuilder<?> builder = PaymentTransactionBuilder.Builder();
        builder.sender(string).receiver(string2).amount(int1)
                .fee(fee).flatFee(flatFee)
                .firstValid(fv).lastValid(lv)
                .genesisHash(genesisHash).genesisID(genesisID);
        if (!string3.isEmpty())
            builder.closeRemainderTo(string3);
        paymentTxn = builder.build();
    }

    @When("I create a transaction with signer with the current transaction.")
    public void i_create_a_transaction_with_signer_with_the_current_transaction() {
        this.txnWithSigner = new AtomicTransactionComposer.TransactionWithSigner(this.paymentTxn, this.txnSigner);
    }

    @When("I create a new method arguments array.")
    public void i_create_a_new_method_arguments_array() {
        this.optionBuilder = new MethodCallOption.MethodCallOptionBuilder();
    }

    @When("I append the current transaction with signer to the method arguments array.")
    public void i_append_the_current_transaction_with_signer_to_the_method_arguments_array() {
        this.optionBuilder.addMethodArgs(this.txnWithSigner);
    }

    private List<AtomicTransactionComposer.ABIValue> splitAndProcessABIArgs(String str) {
        String[] argTokens = str.split(",");
        List<AtomicTransactionComposer.ABIValue> res = new ArrayList<>();

        int argTokenIndex = 0;
        for (Method.Arg argType : methodABI.method.args) {
            if (Method.TxArgTypes.contains(argType.type))
                continue;
            ABIType abiT = ABIType.Of(argType.type);
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
                .setSuggestedParams(suggestedParams)
                .setFirstValid(fv)
                .setLastValid(lv)
                .setFee(fee)
                .setFlatFee(flatFee);
        MethodCallOption optionBuild = optionBuilder.build();
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
        assertThat(atc.status).isEqualTo(AtomicTransactionComposer.AtomicTxComposerStatus.valueOf(string));
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
