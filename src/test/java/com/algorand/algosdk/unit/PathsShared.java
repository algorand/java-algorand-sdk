package com.algorand.algosdk.unit;

import com.algorand.algosdk.unit.utils.TestingUtils;

import com.algorand.algosdk.v2.client.common.Query;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class PathsShared {
    public Query q;

    @Given("mock server recording request paths")
    public void mock_server_recording_request_paths() { }

    @Then("expect the path used to be {string}")
    public void expect_the_path_used_to_be(String string) {
        TestingUtils.verifyPathUrls(q.getRequestUrl(), string);
    }
}
