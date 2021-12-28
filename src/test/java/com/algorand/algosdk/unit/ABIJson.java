package com.algorand.algosdk.unit;

import static org.assertj.core.api.Assertions.assertThat;

import com.algorand.algosdk.abi.Contract;
import com.algorand.algosdk.abi.Interface;
import com.algorand.algosdk.abi.Method;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class ABIJson {
    private enum CHECK_FIELD {
        METHOD,
        INTERFACE,
        CONTRACT
    }

    Method method = null;
    String jsonMethod = null;
    Interface interfaceObj = null;
    String jsonInterface = null;
    Contract contract = null;
    String jsonContract = null;
    CHECK_FIELD state = null;

    @When("I create the Method object from method signature {string}")
    public void i_create_the_method_object_from_method_signature(String string) {
        this.method = new Method(string);
        this.state = CHECK_FIELD.METHOD;
    }

    @When("I serialize the Method object into json")
    public void i_serialize_the_method_object_into_json() throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        this.jsonMethod = om.writeValueAsString(this.method);
    }

    @When("I create the Method object with name {string} first argument type {string} second argument type {string} and return type {string}")
    public void i_create_the_method_object_with_name_first_argument_type_second_argument_type_and_return_type(String string, String string2, String string3, String string4) {
        Method.Arg arg0 = new Method.Arg(null, string2, null);
        Method.Arg arg1 = new Method.Arg(null, string3, null);
        Method.Returns ret = new Method.Returns(string4, null);
        this.method = new Method(string, null, new ArrayList<>(Arrays.asList(arg0, arg1)), ret);
        this.state = CHECK_FIELD.METHOD;
    }

    @When("I create the Method object with name {string} first argument name {string} first argument type {string} second argument name {string} second argument type {string} and return type {string}")
    public void i_create_the_method_object_with_name_first_argument_name_first_argument_type_second_argument_name_second_argument_type_and_return_type(String string, String string2, String string3, String string4, String string5, String string6) {
        Method.Arg arg0 = new Method.Arg(string2, string3, null);
        Method.Arg arg1 = new Method.Arg(string4, string5, null);
        Method.Returns ret = new Method.Returns(string6, null);
        this.method = new Method(string, null, new ArrayList<>(Arrays.asList(arg0, arg1)), ret);
        this.state = CHECK_FIELD.METHOD;
    }

    @When("I create the Method object with name {string} method description {string} first argument type {string} first argument description {string} second argument type {string} second argument description {string} and return type {string}")
    public void i_create_the_method_object_with_name_method_description_first_argument_type_first_argument_description_second_argument_type_second_argument_description_and_return_type(String string, String string2, String string3, String string4, String string5, String string6, String string7) {
        Method.Arg arg0 = new Method.Arg(null, string3, string4);
        Method.Arg arg1 = new Method.Arg(null, string5, string6);
        Method.Returns ret = new Method.Returns(string7, null);
        this.method = new Method(string, string2, new ArrayList<>(Arrays.asList(arg0, arg1)), ret);
        this.state = CHECK_FIELD.METHOD;
    }

    @When("I create an Interface object from the Method object with name {string} and description {string}")
    public void i_create_an_interface_object_from_the_method_object_with_name(String string, String string2) {
        this.interfaceObj = new Interface(string, string2, Collections.singletonList(this.method));
        this.state = CHECK_FIELD.INTERFACE;
    }

    @When("I serialize the Interface object into json")
    public void i_serialize_the_interface_object_into_json() throws JsonProcessingException {
        this.jsonInterface = new ObjectMapper().writeValueAsString(this.interfaceObj);
    }

    @When("I create a Contract object from the Method object with name {string} and description {string}")
    public void i_create_a_contract_object_from_the_method_object_with_name_and_description(String string, String string2) {
        this.contract = new Contract(string, string2, new HashMap<>(), Collections.singletonList(this.method));
        this.contract.networks = new HashMap<>();
        this.state = CHECK_FIELD.CONTRACT;
    }

    @When("I set the Contract's appID to {int} for the network {string}")
    public void i_set_the_contract_s_app_id_to_for_the_network(Integer appID, String network) {
        this.contract.networks.put(network, new Contract.NetworkInfo(appID.longValue()));
    }

    @When("I serialize the Contract object into json")
    public void i_serialize_the_contract_object_into_json() throws JsonProcessingException {
        this.jsonContract = new ObjectMapper().writeValueAsString(this.contract);
    }

    @Then("the txn count should be {int}")
    public void the_txn_count_should_be(Integer int1) {
        assertThat(this.method.getTxnCallCount()).isEqualTo(int1);
    }

    @Then("the method selector should be {string}")
    public void the_method_selector_should_be(String string) throws DecoderException {
        byte[] decodedSelector = Hex.decodeHex(string);
        assertThat(this.method.getSelector()).isEqualTo(decodedSelector);
    }

    static public String getUnitTestFileAsString(String dir, String file) throws IOException {
        String location = "src/test/resources/com/algorand/algosdk/unit/" + dir + "/" + file;
        byte[] res = Files.readAllBytes(Paths.get(location));
        return new String(res);
    }

    @Then("the produced json should equal {string} loaded from {string}")
    public void the_produced_json_should_equal_loaded_from(String string, String string2) throws IOException {
        String jsonStr = getUnitTestFileAsString(string2, string);
        if (this.state == CHECK_FIELD.METHOD)
            assertThat(this.jsonMethod).isEqualTo(jsonStr);
        else if (this.state == CHECK_FIELD.INTERFACE)
            assertThat(this.jsonInterface).isEqualTo(jsonStr);
        else if (this.state == CHECK_FIELD.CONTRACT)
            assertThat(this.jsonContract).isEqualTo(jsonStr);
        else
            throw new IllegalArgumentException("did not decide which state of checking in");
    }

    @Then("the deserialized json should equal the original Method object")
    public void the_deserialized_json_should_equal_the_original_method_object() throws IOException {
        Method deserialized = new ObjectMapper().readValue(this.jsonMethod, Method.class);
        assertThat(this.method).isEqualTo(deserialized);
    }

    @Then("the deserialized json should equal the original Interface object")
    public void the_deserialized_json_should_equal_the_original_interface_object() throws IOException {
        Interface deserialized = new ObjectMapper().readValue(this.jsonInterface, Interface.class);
        assertThat(this.interfaceObj).isEqualTo(deserialized);
    }

    @Then("the deserialized json should equal the original Contract object")
    public void the_deserialized_json_should_equal_the_original_contract_object() throws IOException {
        Contract deserialized = new ObjectMapper().readValue(this.jsonContract, Contract.class);
        assertThat(this.contract).isEqualTo(deserialized);
    }
}
