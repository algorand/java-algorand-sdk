package com.algorand.algosdk.unit;

import com.algorand.algosdk.unit.utils.TestingUtils;
import com.algorand.algosdk.v2.client.common.Query;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import static org.assertj.core.api.Assertions.assertThat;

public class PathsShared {
    public Query q;

    @Given("mock server recording request paths")
    public void mockServerNoop() { }

    @Then("expect the path used to be {string}")
    public void expectPathToBe(String path) {
        TestingUtils.verifyPathUrls(q.getRequestUrl(), path);
    }

    @Then("expect the request to be {string} {string}")
    public void expectMethodAndPathToBe(String method, String path) {
        assertThat(q.getRequestMethod()).isEqualTo(method);
        TestingUtils.verifyPathUrls(q.getRequestUrl(), path);
    }
}
