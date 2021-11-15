package com.algorand.algosdk.unit;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ABIJson {
    Method method = null;
    String jsonMethod = null;
    Interface interfaceObj = null;
    String jsonInterface = null;
    Contract contract = null;
    String jsonContract = null;

    public ABIJson() {
    }

    @When("I create the Method object from method signature {string}")
    public void i_create_the_method_object_from_method_signature(String string) {
        this.method = new Method(string);
    }

    @When("I serialize the Method object into json")
    public void i_serialize_the_method_object_into_json() throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        this.jsonMethod = om.writeValueAsString(this.method);
    }

    @Then("the produced json should equal {string} loaded from {string}")
    public void the_produced_json_should_equal_loaded_from(String string, String string2) {
        assert this.jsonMethod.equals(string);
    }

    @When("I create the Method object with name {string} first argument type {string} second argument type {string} and return type {string}")
    public void i_create_the_method_object_with_name_first_argument_type_second_argument_type_and_return_type(String string, String string2, String string3, String string4) {
        Method.Arg arg0 = new Method.Arg(null, string2, null);
        Method.Arg arg1 = new Method.Arg(null, string3, null);
        Method.Returns ret = new Method.Returns(string4, null);
        this.method = new Method(string, null, new ArrayList<>(Arrays.asList(arg0, arg1)), ret);
    }

    @Then("the deserialized json should equal the original Method object")
    public void the_deserialized_json_should_equal_the_original_method_object() throws IOException {
        Method deserialized = new ObjectMapper().readValue(this.jsonMethod, Method.class);
        assert this.method == deserialized;
    }

    @When("I create the Method object with name {string} first argument name {string} first argument type {string} second argument name {string} second argument type {string} and return type {string}")
    public void i_create_the_method_object_with_name_first_argument_name_first_argument_type_second_argument_name_second_argument_type_and_return_type(String string, String string2, String string3, String string4, String string5, String string6) {
        Method.Arg arg0 = new Method.Arg(string2, string3, null);
        Method.Arg arg1 = new Method.Arg(string4, string5, null);
        Method.Returns ret = new Method.Returns(string6, null);
        this.method = new Method(string, null, new ArrayList<>(Arrays.asList(arg0, arg1)), ret);
    }

    @When("I create the Method object with name {string} method description {string} first argument type {string} first argument description {string} second argument type {string} second argument description {string} and return type {string}")
    public void i_create_the_method_object_with_name_method_description_first_argument_type_first_argument_description_second_argument_type_second_argument_description_and_return_type(String string, String string2, String string3, String string4, String string5, String string6, String string7) {
        Method.Arg arg0 = new Method.Arg(null, string3, string4);
        Method.Arg arg1 = new Method.Arg(null, string5, string6);
        Method.Returns ret = new Method.Returns(string7, null);
        this.method = new Method(string, string2, new ArrayList<>(Arrays.asList(arg0, arg1)), ret);
    }

    @Then("the txn count should be {int}")
    public void the_txn_count_should_be(Integer int1) {
        assert this.method.getTxnCallCount() == int1;
    }

    @Then("the method selector should be {string}")
    public void the_method_selector_should_be(String string) throws DecoderException {
        byte[] decodedSelector = Hex.decodeHex(string);
        assert Arrays.equals(this.method.getSelector(), decodedSelector);
    }

    @When("I create an Interface object from the Method object with name {string}")
    public void i_create_an_interface_object_from_the_method_object_with_name(String string) {
        this.interfaceObj = new Interface(string, new ArrayList<>(Collections.singletonList(this.method)));
    }

    @When("I serialize the Interface object into json")
    public void i_serialize_the_interface_object_into_json() throws JsonProcessingException {
        this.jsonInterface = new ObjectMapper().writeValueAsString(this.interfaceObj);
    }

    @Then("the deserialized json should equal the original Interface object")
    public void the_deserialized_json_should_equal_the_original_interface_object() throws IOException {
        Interface deserialized = new ObjectMapper().readValue(this.jsonInterface, Interface.class);
        assert deserialized.equals(this.interfaceObj);
    }

    @When("I create a Contract object from the Method object with name {string} and appId {int}")
    public void i_create_a_contract_object_from_the_method_object_with_name_and_app_id(String string, Integer int1) {
        this.contract = new Contract(string, int1, new ArrayList<>(Collections.singletonList(this.method)));
    }

    @When("I serialize the Contract object into json")
    public void i_serialize_the_contract_object_into_json() throws JsonProcessingException {
        this.jsonContract = new ObjectMapper().writeValueAsString(this.contract);
    }

    @Then("the deserialized json should equal the original Contract object")
    public void the_deserialized_json_should_equal_the_original_contract_object() throws IOException {
        Contract deserialized = new ObjectMapper().readValue(this.jsonContract, Contract.class);
        assert deserialized.equals(this.contract);
    }
}
