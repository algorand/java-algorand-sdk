package com.algorand.algosdk.unit;

import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.util.Encoder;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class ProgramSanityCheck {
    byte[] seeminglyProgram;
    String actualErrMsg;

    @Given("a base64 encoded program bytes for heuristic sanity check {string}")
    public void takeB64encodedBytes(String b64encodedBytes) {
        seeminglyProgram = Encoder.decodeFromBase64(b64encodedBytes);
    }

    @When("I start heuristic sanity check over the bytes")
    public void heuristicCheckOverBytes() {
        try {
            new LogicsigSignature(seeminglyProgram);
        } catch (Exception e) {
            actualErrMsg = e.getMessage();
        }
    }

    @Then("if the heuristic sanity check throws an error, the error contains {string}")
    public void checkErrorIfMatching(String errMsg) {
        if (errMsg != null && !errMsg.isEmpty())
            assertThat(actualErrMsg).contains(errMsg);
        else
            assertThat(actualErrMsg).isNullOrEmpty();
    }
}
