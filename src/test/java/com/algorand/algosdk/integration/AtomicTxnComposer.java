package com.algorand.algosdk.integration;

import com.algorand.algosdk.abi.ABIType;
import com.algorand.algosdk.abi.Method;
import com.algorand.algosdk.cucumber.shared.TransactionSteps;
import com.algorand.algosdk.transaction.*;
import com.algorand.algosdk.util.Encoder;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AtomicTxnComposer {

    Stepdefs base;
    Applications applications;
    AtomicTransactionComposer atc;
    TransactionSteps transSteps;
    TxnSigner transSigner;
    TransactionWithSigner transWithSigner;
    Method method;
    MethodCallParams.Builder optionBuilder;
    AtomicTransactionComposer.ExecuteResult execRes;

    public AtomicTxnComposer(Stepdefs stepdefs, Applications apps, TransactionSteps steps) {
        base = stepdefs;
        applications = apps;
        applications.transientAccount.clients.v2Client = base.aclv2;
        applications.transientAccount.base = base;
        transSteps = steps;
        transSteps.transAcc = apps.transientAccount;
    }

    @Given("suggested transaction parameters from the algod v2 client")
    public void suggested_transaction_parameters_from_the_algod_v2_client() throws IOException {
        base.aClient();
        base.getParams();

        transSteps.suggestedParams = base.params;
        transSteps.genesisHash = transSteps.suggestedParams.getGenesishashb64();
        transSteps.genesisID = transSteps.suggestedParams.getGenesisID();
        transSteps.fee = transSteps.suggestedParams.getFee();
        transSteps.fv = transSteps.suggestedParams.getLastRound();
        transSteps.lv = transSteps.fv.add(BigInteger.valueOf(1000));
    }

    @Given("a new AtomicTransactionComposer")
    public void a_new_atomic_transaction_composer() {
        this.atc = new AtomicTransactionComposer();
    }

    @When("I make a transaction signer for the transient account.")
    public void i_make_a_transaction_signer_for_the_transient_account() {
        transSigner = applications.transientAccount.transientAccount.getTransactionSigner();
    }

    @When("I create a transaction with signer with the current transaction.")
    public void i_create_a_transaction_with_signer_with_the_current_transaction() {
        transWithSigner = new TransactionWithSigner(transSteps.builtTransaction, transSigner);
    }

    @When("I add the current transaction with signer to the composer.")
    public void i_add_the_current_transaction_with_signer_to_the_composer() {
        atc.addTransaction(transWithSigner);
    }

    @Then("I clone the composer.")
    public void i_clone_the_composer() throws IOException {
        atc = atc.cloneComposer();
    }

    @Then("I gather signatures with the composer.")
    public void i_gather_signatures_with_the_composer() throws Exception {
        atc.gatherSignatures();
    }

    @When("I create the Method object from method signature {string}")
    public void i_create_the_method_object_from_method_signature(String string) {
        method = new Method(string);
    }

    @When("I create a new method arguments array.")
    public void i_create_a_new_method_arguments_array() {
         optionBuilder = new MethodCallParams.Builder();
    }

    @When("I append the current transaction with signer to the method arguments array.")
    public void i_append_the_current_transaction_with_signer_to_the_method_arguments_array() {
        this.optionBuilder.addMethodArgs(this.transWithSigner);
    }

    private List<Object> splitAndProcessABIArgs(String str) {
        String[] argTokens = str.split(",");
        List<Object> res = new ArrayList<>();

        int argTokenIndex = 0;
        for (Method.Arg argType : method.args) {
            if (Method.TxArgTypes.contains(argType.type))
                continue;
            ABIType abiT = ABIType.valueOf(argType.type);
            byte[] abiEncoded = Encoder.decodeFromBase64(argTokens[argTokenIndex]);
            res.add(abiT.decode(abiEncoded));
            argTokenIndex++;
        }

        return res;
    }

    @When("I append the encoded arguments {string} to the method arguments array.")
    public void i_append_the_encoded_arguments_to_the_method_arguments_array(String string) {
        List<Object> processedABIArgs = splitAndProcessABIArgs(string);
        for (Object arg : processedABIArgs)
            this.optionBuilder.addMethodArgs(arg);
    }

    @Then("I execute the current transaction group with the composer.")
    public void i_execute_the_current_transaction_group_with_the_composer() throws Exception {
        execRes = atc.execute(applications.base.aclv2, 5);
    }

    @Then("The app should have returned {string}.")
    public void the_app_should_have_returned(String string) {
        assertThat(execRes.methodResults.size()).isEqualTo(1);

        AtomicTransactionComposer.ReturnValue execRetVal = execRes.methodResults.get(0);
        assertThat(execRetVal.parseError).isNull();

        if (string.isEmpty()) {
            assertThat(this.method.returns.type).isEqualTo("void");
            return;
        }
        Object parsed = execRetVal.value;
        Object idealRes = this.method.returns.parsedType.decode(Encoder.decodeFromBase64(string));
        assertThat(parsed).isEqualTo(idealRes);
    }

    @Then("The composer should have a status of {string}.")
    public void the_composer_should_have_a_status_of(String string) {
        assertThat(atc.getStatus()).isEqualTo(AtomicTransactionComposer.Status.valueOf(string));
    }

    @When("I add a method call with the transient account, the current application, suggested params, on complete {string}, current transaction signer, current method arguments.")
    public void i_add_a_method_call_with_the_signing_account_the_current_application_suggested_params_on_complete_current_transaction_signer_current_method_arguments(String string) {
        String senderAddress = applications.transientAccount.transientAccount.getAddress().toString();
        optionBuilder
                .setOnComplete(Transaction.OnCompletion.String(string))
                .setSender(senderAddress)
                .setSigner(transSigner)
                .setAppID(applications.appId)
                .setMethod(method)
                .setSuggestedParams(transSteps.suggestedParams)
                .setFirstValid(transSteps.fv)
                .setLastValid(transSteps.lv)
                .setFee(transSteps.fee);
        MethodCallParams optionBuild = optionBuilder.build();
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
}
