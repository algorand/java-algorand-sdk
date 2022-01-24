package com.algorand.algosdk.integration;

import com.algorand.algosdk.abi.ABIType;
import com.algorand.algosdk.abi.Method;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.crypto.TEALProgram;
import com.algorand.algosdk.cucumber.shared.TransactionSteps;
import com.algorand.algosdk.transaction.*;
import com.algorand.algosdk.util.*;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
    SplitAndProcessMethodArgs abiArgProcessor;
    Long appID;
    List<Method> composerMethods;
    String nonce;

    public AtomicTxnComposer(Stepdefs stepdefs, Applications apps, TransactionSteps steps) {
        base = stepdefs;
        applications = apps;
        applications.transientAccount.clients.v2Client = base.aclv2;
        applications.transientAccount.base = base;
        transSteps = steps;
        transSteps.transAcc = apps.transientAccount;
    }

    @Given("I add the nonce {string}")
    public void addNonce(String nonce) {
        this.nonce = nonce;
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

    @Given("an application id {int}")
    public void an_application_id(Integer int1) {
        this.appID = int1.longValue();
    }

    @Given("a new AtomicTransactionComposer")
    public void a_new_atomic_transaction_composer() {
        this.atc = new AtomicTransactionComposer();
        composerMethods = new ArrayList<>();
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
         abiArgProcessor = new SplitAndProcessMethodArgs(method);
    }

    @When("I append the current transaction with signer to the method arguments array.")
    public void i_append_the_current_transaction_with_signer_to_the_method_arguments_array() {
        this.optionBuilder.addMethodArgs(this.transWithSigner);
    }

    @When("I append the encoded arguments {string} to the method arguments array.")
    public void i_append_the_encoded_arguments_to_the_method_arguments_array(String string) {
        List<Object> processedABIArgs = abiArgProcessor.splitAndProcessMethodArgs(string, applications.rememberedAppIds);
        for (Object arg : processedABIArgs)
            this.optionBuilder.addMethodArgs(arg);
    }

    @Then("I execute the current transaction group with the composer.")
    public void i_execute_the_current_transaction_group_with_the_composer() throws Exception {
        execRes = atc.execute(applications.base.aclv2, 5);
    }

    @Then("The app should have returned {string}.")
    public void the_app_should_have_returned(String string) {
        String[] splitEncoding = string.split(",");
        assertThat(execRes.methodResults.size()).isEqualTo(splitEncoding.length);
        assertThat(execRes.methodResults.size()).isEqualTo(composerMethods.size());

        for (int i = 0; i < splitEncoding.length; i++) {
            AtomicTransactionComposer.ReturnValue execRetVal = execRes.methodResults.get(i);
            Method currMethod = composerMethods.get(i);
            assertThat(execRetVal.parseError).isNull();

            if (splitEncoding[i].isEmpty()) {
                assertThat(currMethod.returns.type).isEqualTo(Method.Returns.VoidRetType);
                continue;
            }

            Object parsed = execRetVal.value;
            Object idealRes = currMethod.returns.parsedType.decode(Encoder.decodeFromBase64(splitEncoding[i]));
            assertThat(parsed).isEqualTo(idealRes);
        }
    }

    @Then("The app should have returned ABI types {string}.")
    public void the_app_should_have_returned_abi_types(String abiTypeStr) {
        String[] abiTypes = abiTypeStr.split(":");
        assertThat(abiTypes.length).isEqualTo(execRes.methodResults.size());

        for (int i = 0; i < abiTypes.length; i++) {
            assertThat(execRes.methodResults.get(i).parseError).isNull();
            if (abiTypes[i].equals("void")) {
                assertThat(execRes.methodResults.get(i).rawValue).isNull();
                continue;
            }
            ABIType tokenT = ABIType.valueOf(abiTypes[i]);
            Object decoded = tokenT.decode(execRes.methodResults.get(i).rawValue);
            byte[] encoded = tokenT.encode(decoded);
            assertThat(encoded).isEqualTo(execRes.methodResults.get(i).rawValue);
        }
    }

    // WE ASSUME WITNESS IS byte[17] IN THIS TEST
    static byte[] convertToByteArray(Object witnessObj) {
        Object[] witnessObjArr = (Object[]) witnessObj;
        byte[] witness = new byte[witnessObjArr.length];
        for (int i = 0; i < witnessObjArr.length; i++)
            witness[i] = (byte) witnessObjArr[i];
        return witness;
    }

    static BigInteger computeFromWitness(byte[] witness) throws NoSuchAlgorithmException {
        byte[] witnessChecksum = Arrays.copyOfRange(Digester.digest(witness), 0, 8);
        return new BigInteger(1, witnessChecksum);
    }

    @Then("The {int}th atomic result for randomInt\\({int}) proves correct")
    public void the_i_th_atomic_result_for_random_int_proves_correct(Integer resultIndex, Integer input)
            throws NoSuchAlgorithmException {
        AtomicTransactionComposer.ReturnValue iThRet = execRes.methodResults.get(resultIndex);
        assertThat(iThRet.parseError).isNull();
        Object[] randIntAndWitness = (Object[]) iThRet.value;
        BigInteger randInt = (BigInteger) randIntAndWitness[0];
        byte[] witness = convertToByteArray(randIntAndWitness[1]);
        BigInteger x = computeFromWitness(witness);
        assertThat(x.mod(BigInteger.valueOf(input))).isEqualTo(randInt);
    }

    @Then("The {int}th atomic result for randElement\\({string}) proves correct")
    public void the_i_th_atomic_result_for_random_string_proves_correct(Integer resultIndex, String input)
            throws NoSuchAlgorithmException {
        AtomicTransactionComposer.ReturnValue iThRet = execRes.methodResults.get(resultIndex);
        assertThat(iThRet.parseError).isNull();
        Object[] randByteAndWitness = (Object[]) iThRet.value;
        byte randByte = (byte) randByteAndWitness[0];
        byte[] witness = convertToByteArray(randByteAndWitness[1]);
        int quotient = computeFromWitness(witness).mod(BigInteger.valueOf(input.length())).intValue();
        assertThat(input.getBytes(StandardCharsets.UTF_8)[quotient]).isEqualTo(randByte);
    }

    @Then("The {int}th atomic result for {string} satisfies the regex {string}")
    public void the_i_th_atomic_result_for_method_satisfies_regex(Integer resultIndex, String methodName, String regexExpr) {
        assertThat(methodName).isEqualTo("spin()");
        AtomicTransactionComposer.ReturnValue iThRet = execRes.methodResults.get(resultIndex);
        Object[] spinResultAndWitness = (Object[]) iThRet.value;
        byte[] spinResult = convertToByteArray(spinResultAndWitness[0]);
        String spinResultStr = new String(spinResult, StandardCharsets.UTF_8);
        assertThat(Pattern.compile(regexExpr).matcher(spinResultStr).matches()).isTrue();
    }

    @SuppressWarnings("unchecked")
    @Then("I can dig the {int}th atomic result with path {string} and see the value {string}")
    public void digDownIthATCResWithPathForValue(Integer resIndex, String pathDown, String value) {
        String[] pathKeys = pathDown.split("[.]");
        PendingTransactionResponse transactionInfo = execRes.methodResults.get(resIndex).txInfo;
        Object jsonGeneric;
        try {
            jsonGeneric = Encoder.decodeFromMsgPack(Encoder.encodeToMsgPack(transactionInfo), Object.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("cannot encode txInfo to msgpack, then convert back to generic Object");
        }
        for (String currentKey : pathKeys) {
            boolean itIsIntKey = true;
            int intKey = 0;
            try {
                intKey = Integer.parseInt(currentKey);
            } catch (NumberFormatException e) {
                itIsIntKey = false;
            }

            Object genericValue;

            if (itIsIntKey) {
                Object[] genericArr;
                try {
                    genericArr = GenericObjToArray.unifyToArrayOfObjects(jsonGeneric);
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                            "path component " + currentKey + " is an array index, but the parent is not an array: parent type " + jsonGeneric.getClass()
                    );
                }
                if (intKey < 0 || intKey >= genericArr.length)
                    throw new IllegalArgumentException(
                            "path component " + currentKey + " is an array index that is outside scope: parent length " + genericArr.length
                    );
                genericValue = genericArr[intKey];
            } else {
                Map<String, Object> castedToMap;
                try {
                    castedToMap = (Map<String, Object>) jsonGeneric;
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                            "path component " + currentKey + " is expected to be an object field, but it is not there"
                    );
                }
                try {
                    genericValue = castedToMap.get(currentKey);
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                            "path component " + currentKey + " is expected to get an object field, but we cannot retrieve: " + e.getMessage()
                    );
                }
            }
            jsonGeneric = genericValue;
        }

        if (jsonGeneric instanceof String) {
            assertThat((String) jsonGeneric).isEqualTo(value);
        } else if (jsonGeneric instanceof Integer) {
            assertThat((Integer) jsonGeneric).isEqualTo(Integer.valueOf(value));
        } else
            throw new IllegalArgumentException("cannot infer type for target json value");
    }

    @Then("I dig into the paths {string} of the resulting atomic transaction tree I see group ids and they are all the same")
    public void checkInnerTxnGroupIDs(String colonPathString) {
        String[] pathKeys = colonPathString.split(":");

        List<List<Integer>> paths = new ArrayList<>();
        for (String path : pathKeys) {
            String[] splitPath = path.split(",");
            List<Integer> integerPath = new ArrayList<>();
            for (String key : splitPath)
                integerPath.add(Integer.parseInt(key));
            paths.add(integerPath);
        }

        List<PendingTransactionResponse> txInfoToCheck = new ArrayList<>();
        for (List<Integer> path : paths) {
            PendingTransactionResponse current = execRes.methodResults.get(path.get(0)).txInfo;
            for (int i = 1; i < path.size(); i++)
                current = current.innerTxns.get(path.get(i));
            txInfoToCheck.add(current);
        }

        Digest groupID = txInfoToCheck.get(0).txn.tx.group;
        for (int i = 1; i < txInfoToCheck.size(); i++) {
            Digest currentGroupID = txInfoToCheck.get(i).txn.tx.group;
            if (!groupID.equals(currentGroupID))
                throw new IllegalArgumentException("Group hashes differ: " + groupID + " != " + currentGroupID);
        }
    }

    @Then("The composer should have a status of {string}.")
    public void the_composer_should_have_a_status_of(String string) {
        assertThat(atc.getStatus()).isEqualTo(AtomicTransactionComposer.Status.valueOf(string));
    }

    @When("I add a method call with the transient account, the current application, suggested params, on complete {string}, current transaction signer, current method arguments.")
    public void i_add_a_method_call_with_the_signing_account_the_current_application_suggested_params_on_complete_current_transaction_signer_current_method_arguments(String onComplete) {
        String senderAddress = applications.transientAccount.transientAccount.getAddress().toString();
        composerMethods.add(method);

        optionBuilder
                .setOnComplete(Transaction.OnCompletion.String(onComplete))
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

    @When("I add a nonced method call with the transient account, the current application, suggested params, on complete {string}, current transaction signer, current method arguments.")
    public void i_add_a_nonced_method_call_with_the_transient_account_the_current_application_suggested_params_on_complete_current_transaction_signer_current_method_arguments(String onComplete) {
        String senderAddress = applications.transientAccount.transientAccount.getAddress().toString();
        composerMethods.add(method);

        optionBuilder
                .setOnComplete(Transaction.OnCompletion.String(onComplete))
                .setSender(senderAddress)
                .setSigner(transSigner)
                .setAppID(applications.appId)
                .setMethod(method)
                .setNote(("I should be unique thanks to this nonce: " + nonce).getBytes(StandardCharsets.UTF_8))
                .setSuggestedParams(transSteps.suggestedParams)
                .setFirstValid(transSteps.fv)
                .setLastValid(transSteps.lv)
                .setFee(transSteps.fee);
        MethodCallParams optionBuild = optionBuilder.build();
        atc.addMethodCall(optionBuild);
    }

    @When("I add a method call with the transient account, the current application, suggested params, on complete {string}, current transaction signer, current method arguments, approval-program {string}, clear-program {string}, global-bytes {int}, global-ints {int}, local-bytes {int}, local-ints {int}, extra-pages {int}.")
    public void i_add_a_method_call_with_the_transient_account_the_current_application_suggested_params_on_complete_current_transaction_signer_current_method_arguments_approval_program_clear_program_global_bytes_global_ints_local_bytes_local_ints_extra_pages(String string, String string2, String string3, Integer int1, Integer int2, Integer int3, Integer int4, Integer int5) {
        byte[] tealApproval, tealClear;
        try {
            tealApproval = ResourceUtils.readResource(string2);
            tealClear = ResourceUtils.readResource(string3);
        } catch (Exception e) {
            throw new IllegalArgumentException("cannot read resource from specified TEAL files");
        }
        composerMethods.add(method);

        String senderAddress = applications.transientAccount.transientAccount.getAddress().toString();

        optionBuilder
                .setOnComplete(Transaction.OnCompletion.String(string))
                .setSender(senderAddress)
                .setSigner(transSigner)
                .setAppID(appID)
                .setMethod(method)
                .setSuggestedParams(this.transSteps.suggestedParams)
                .setFirstValid(this.transSteps.fv)
                .setLastValid(this.transSteps.lv)
                .setFee(this.transSteps.fee)
                .setFlatFee(this.transSteps.flatFee)
                .setApprovalProgram(new TEALProgram(tealApproval))
                .setClearProgram(new TEALProgram(tealClear))
                .setGlobalBytes(int1.longValue())
                .setGlobalInts(int2.longValue())
                .setLocalBytes(int3.longValue())
                .setLocalInts(int4.longValue())
                .setExtraPages(int5.longValue());
        MethodCallParams optionBuild = optionBuilder.build();
        atc.addMethodCall(optionBuild);
    }

    @When("I add a method call with the transient account, the current application, suggested params, on complete {string}, current transaction signer, current method arguments, approval-program {string}, clear-program {string}.")
    public void i_add_a_method_call_with_the_transient_account_the_current_application_suggested_params_on_complete_current_transaction_signer_current_method_arguments_approval_program_clear_program(String string, String string2, String string3) {
        byte[] tealApproval, tealClear;
        try {
            tealApproval = ResourceUtils.readResource(string2);
            tealClear = ResourceUtils.readResource(string3);
        } catch (Exception e) {
            throw new IllegalArgumentException("cannot read resource from specified TEAL files");
        }
        composerMethods.add(method);

        String senderAddress = applications.transientAccount.transientAccount.getAddress().toString();

        optionBuilder
                .setOnComplete(Transaction.OnCompletion.String(string))
                .setSender(senderAddress)
                .setSigner(transSigner)
                .setAppID(applications.appId)
                .setMethod(method)
                .setSuggestedParams(this.transSteps.suggestedParams)
                .setFirstValid(this.transSteps.fv)
                .setLastValid(this.transSteps.lv)
                .setFee(this.transSteps.fee)
                .setFlatFee(this.transSteps.flatFee)
                .setApprovalProgram(new TEALProgram(tealApproval))
                .setClearProgram(new TEALProgram(tealClear));
        MethodCallParams optionBuild = optionBuilder.build();
        atc.addMethodCall(optionBuild);
    }

    @When("I build the transaction group with the composer. If there is an error it is {string}.")
    public void i_build_the_transaction_group_with_the_composer_if_there_is_an_error_it_is(String errStr) {
        String inferredError = "";
        try {
            atc.buildGroup();
        } catch (IllegalArgumentException e) {
            inferredError = "zero group size error";
        } catch (IOException e) {
            inferredError = e.getMessage();
        }
        assertThat(inferredError).isEqualTo(errStr);
    }
}
