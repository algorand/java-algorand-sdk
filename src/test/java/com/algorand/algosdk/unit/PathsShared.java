package com.algorand.algosdk.unit;

import com.algorand.algosdk.unit.utils.TestingUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import static org.assertj.core.api.Assertions.assertThat;

public class PathsShared {
    public String requestUrl;

    @Given("mock server recording request paths")
    public void mock_server_recording_request_paths() { }

    @Then("expect the path used to be {string}")
    public void expect_the_path_used_to_be(String string) {
        TestingUtils.verifyPathUrls(this.requestUrl, string);
    }
}
