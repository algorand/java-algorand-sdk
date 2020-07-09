package com.algorand.algosdk.unit;

import static com.algorand.algosdk.unit.utils.TestingUtils.verifyResponse;

import java.io.IOException;
import java.math.BigInteger;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.unit.utils.ClientMocker;
import com.algorand.algosdk.v2.client.common.IndexerClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.AccountResponse;
import com.algorand.algosdk.v2.client.model.AccountsResponse;
import com.algorand.algosdk.v2.client.model.AssetBalancesResponse;
import com.algorand.algosdk.v2.client.model.AssetResponse;
import com.algorand.algosdk.v2.client.model.AssetsResponse;
import com.algorand.algosdk.v2.client.model.Block;
import com.algorand.algosdk.v2.client.model.TransactionsResponse;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class IndexerResponses {
    IndexerClient client;
    ResponsesShared shared;

    public IndexerResponses(ResponsesShared shared) {
        this.shared = shared;
        this.client = new IndexerClient("localhost", 123, "");
    }

    // All possible responses.
    Response<Block> blockResponse;
    Response<AccountResponse> accountResponse;
    Response<AccountsResponse> accountsResponse;
    Response<AssetResponse> assetResponse;
    Response<AssetBalancesResponse> assetBalancesResponse;
    Response<TransactionsResponse> transactionsResponse;
    Response<AssetsResponse> assetsResponse;

    @When("we make any LookupAssetBalances call")
    public void anyLookupAssetBalancesCall() throws Exception {
        ClientMocker.infect(client);
        assetBalancesResponse = client.lookupAssetBalances(0l).execute();
    }

    @When("we make any LookupAssetTransactions call")
    public void we_make_any_LookupAssetTransactions_call() throws Exception {
        ClientMocker.infect(client);
        transactionsResponse = client.lookupAssetTransactions(123L).execute();
    }

    @When("we make any LookupAccountTransactions call")
    public void anyLookupAccountTransactionsCall() throws Exception {
        ClientMocker.infect(client);
        transactionsResponse = client.lookupAccountTransactions(new Address()).execute();
    }

    @When("we make any LookupBlock call")
    public void we_make_any_LookupBlock_call() throws Exception {
        ClientMocker.infect(client);
        blockResponse = client.lookupBlock(Long.MAX_VALUE).execute();
    }

    @When("we make any LookupAccountByID call")
    public void we_make_any_LookupAccountByID_call() throws Exception {
        ClientMocker.infect(client);
        accountResponse = client.lookupAccountByID(new Address()).execute();
    }

    @When("we make any LookupAssetByID call")
    public void we_make_any_LookupAssetByID_call() throws Exception {
        ClientMocker.infect(client);
        assetResponse = client.lookupAssetByID(99L).execute();
    }

    @When("we make any SearchAccounts call")
    public void we_make_any_SearchAccounts_call() throws Exception {
        ClientMocker.infect(client);
        accountsResponse = client.searchForAccounts().execute();
    }

    @When("we make any SearchForTransactions call")
    public void we_make_any_SearchForTransactions_call() throws Exception {
        ClientMocker.infect(client);
        transactionsResponse = client.searchForTransactions().execute();
    }

    @When("we make any SearchForAssets call")
    public void we_make_any_SearchForAssets_call() throws Exception {
        ClientMocker.infect(client);
        assetsResponse = client.searchForAssets().execute();
    }

    @Then("expect error string to contain {string}")
    public void expect_error_string_to_contain(String string) {
        if (StringUtils.isNotEmpty(string) && !string.equals("nil")) {
            // Why do no error cases test an error?
            Assertions.fail("No tests were implementing this so neither did I");
        }
    }

    @Then("the parsed LookupAssetBalances response should be valid on round {long}, "
            + "and contain an array of len {int} "
            + "and element number {int} "
            + "should have address {string} "
            + "amount {biginteger} "
            + "and frozen state {string}")
    public void verifyLookupAssetBalancesResponse(
            Long round, Integer length, Integer index, String string, BigInteger amount, String isFrozen) throws IOException {
        verifyResponse(assetBalancesResponse, shared.bodyFile);
    }

    @Then("the parsed LookupAssetTransactions response should be valid on round {long}, "
            + "and contain an array of len {long} "
            + "and element number {long} "
            + "should have sender {string}")
    public void verifyLookupAssetTransactionsResponse(
            Long round, Long length, Long index, String sender) throws IOException {
        verifyResponse(transactionsResponse, shared.bodyFile);
    }

    @Then("the parsed LookupAccountTransactions response should be valid on round {long}, "
            + "and contain an array of len {long} "
            + "and element number {long} "
            + "should have sender {string}")
    public void the_parsed_LookupAccountTransactions_response_should_be_valid_on_round_and_contain_an_array_of_len_and_element_number_should_have_sender(
            Long int1, Long int2, Long int3, String string) throws IOException {
        verifyResponse(transactionsResponse, shared.bodyFile);
    }


    @Then("the parsed LookupBlock response should have previous block hash {string}")
    public void the_parsed_LookupBlock_response_should_have_previous_block_hash(String string) throws IOException {
        verifyResponse(blockResponse, shared.bodyFile);
    }


    @Then("the parsed LookupAccountByID response should have address {string}")
    public void the_parsed_LookupAccountByID_response_should_have_address(String string) throws IOException {
        verifyResponse(accountResponse, shared.bodyFile);
    }


    @Then("the parsed LookupAssetByID response should have index {long}")
    public void the_parsed_LookupAssetByID_response_should_have_index(Long int1) throws IOException {
        verifyResponse(assetResponse, shared.bodyFile);
    }


    @Then("the parsed SearchAccounts response should be valid on round {long} "
            + "and the array should be of len {long} "
            + "and the element at index {long} "
            + "should have address {string}")
    public void the_parsed_SearchAccounts_response_should_be_valid_on_round_and_the_array_should_be_of_len_and_the_element_at_index_should_have_address(
            Long int1, Long int2, Long int3, String string) throws IOException {
        verifyResponse(accountsResponse, shared.bodyFile);
    }

    @Then("the parsed SearchForTransactions response should be valid on round {long} "
            + "and the array should be of len {long} "
            + "and the element at index {long} "
            + "should have sender {string}")
    public void the_parsed_SearchForTransactions_response_should_be_valid_on_round_and_the_array_should_be_of_len_and_the_element_at_index_should_have_sender(
            Long int1, Long int2, Long int3, String string) throws IOException {
        verifyResponse(transactionsResponse, shared.bodyFile);
    }


    @Then("the parsed SearchForAssets response should be valid on round {long} "
            + "and the array should be of len {long} "
            + "and the element at index {long} "
            + "should have asset index {long}")
    public void the_parsed_SearchForAssets_response_should_be_valid_on_round_and_the_array_should_be_of_len_and_the_element_at_index_should_have_asset_index(
            Long int1, Long int2, Long int3, Long int4) throws IOException {
        verifyResponse(assetsResponse, shared.bodyFile);
    }

    @When("the parsed SearchAccounts response should be valid on round {int} and the array should be of len {int} and the element at index {int} should have authorizing address {string}")
    public void the_parsed_SearchAccounts_response_should_be_valid_on_round_and_the_array_should_be_of_len_and_the_element_at_index_should_have_authorizing_address(Integer int1, Integer int2, Integer int3, String string) throws IOException {
        verifyResponse(accountsResponse, shared.bodyFile);
    }

    @When("the parsed SearchForTransactions response should be valid on round {int} and the array should be of len {int} and the element at index {int} should have rekey-to {string}")
    public void the_parsed_SearchForTransactions_response_should_be_valid_on_round_and_the_array_should_be_of_len_and_the_element_at_index_should_have_rekey_to(Integer int1, Integer int2, Integer int3, String string) throws IOException {
        verifyResponse(transactionsResponse, shared.bodyFile);
    }
}
