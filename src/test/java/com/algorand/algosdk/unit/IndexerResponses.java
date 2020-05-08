package com.algorand.algosdk.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.unit.utils.TestIndexerClient;
import com.algorand.algosdk.v2.client.common.Response;

import com.algorand.algosdk.v2.client.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;

public class IndexerResponses {
	static ObjectMapper mapper = new ObjectMapper();

	File bodyFile;
	TestIndexerClient mockClient = new TestIndexerClient();

	// All possible responses.
	Response<Block> blockResponse;
	Response<AccountResponse> accountResponse;
	Response<AccountsResponse> accountsResponse;
	Response<AssetResponse> assetResponse;
	Response<AssetBalancesResponse> assetBalancesResponse;
	Response<TransactionsResponse> transactionsResponse;
	Response<AssetsResponse> assetsResponse;

	@Given("mock http responses in {string} loaded from {string}")
	public void mock_http_responses_in_loaded_from(String file, String dir) throws IOException {
		this.bodyFile = new File("src/test/resources/com/algorand/algosdk/unit/" + dir + "/" + file);
		assertThat(this.bodyFile).exists();
		this.mockClient.addResponse(200, "application/json", bodyFile);
	}

	@When("we make any LookupAssetBalances call")
	public void anyLookupAssetBalancesCall() throws Exception {
		 assetBalancesResponse = mockClient.lookupAssetBalances(0l).execute();
	}

	@When("we make any LookupAssetTransactions call")
	public void we_make_any_LookupAssetTransactions_call() throws Exception {
	    transactionsResponse = mockClient.lookupAssetTransactions(123L).execute();
	}

	@When("we make any LookupAccountTransactions call")
	public void anyLookupAccountTransactionsCall() throws Exception {
		transactionsResponse = mockClient.lookupAccountTransactions(new Address()).execute();
	}

	@When("we make any LookupBlock call")
	public void we_make_any_LookupBlock_call() throws Exception {
	    blockResponse = mockClient.lookupBlock(Long.MAX_VALUE).execute();
	}

	@When("we make any LookupAccountByID call")
	public void we_make_any_LookupAccountByID_call() throws Exception {
	    accountResponse = mockClient.lookupAccountByID(new Address()).execute();
	}

	@When("we make any LookupAssetByID call")
	public void we_make_any_LookupAssetByID_call() throws Exception {
	    assetResponse = mockClient.lookupAssetByID(99L).execute();
	}

	@When("we make any SearchAccounts call")
	public void we_make_any_SearchAccounts_call() throws Exception {
	    accountsResponse = mockClient.searchForAccounts().execute();
	}

	@When("we make any SearchForTransactions call")
	public void we_make_any_SearchForTransactions_call() throws Exception {
	    transactionsResponse = mockClient.searchForTransactions().execute();
	}

	@When("we make any SearchForAssets call")
	public void we_make_any_SearchForAssets_call() throws Exception {
	    assetsResponse = mockClient.searchForAssets().execute();
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
			Long round, Integer length, Integer index, String string, BigInteger amount, String isFrozen) throws Exception {
		AssetBalancesResponse ab = assetBalancesResponse.body();

		assertThat(assetBalancesResponse.isSuccessful()).isTrue();
		assertThat(ab).isNotNull();

		String expectedString = new String(Files.readAllBytes(bodyFile.toPath()));
		String actualString = ab.toString();

		JsonNode expectedNode = mapper.readTree(expectedString);
		JsonNode actualNode = mapper.readTree(actualString);

		assertThat(expectedNode).isEqualTo(actualNode);
	}

	@Then("the parsed LookupAssetTransactions response should be valid on round {long}, "
			+ "and contain an array of len {long} "
			+ "and element number {long} "
			+ "should have sender {string}")
	public void verifyLookupAssetTransactionsResponse(
			Long round, Long length, Long index, String sender) {
	}

	@Then("the parsed LookupAccountTransactions response should be valid on round {long}, "
			+ "and contain an array of len {long} "
			+ "and element number {long} "
			+ "should have sender {string}")
	public void the_parsed_LookupAccountTransactions_response_should_be_valid_on_round_and_contain_an_array_of_len_and_element_number_should_have_sender(
			Long int1, Long int2, Long int3, String string) {
		// Write code here that turns the phrase above into concrete actions
		throw new io.cucumber.java.PendingException();

	}


	@Then("the parsed LookupBlock response should have previous block hash {string}")
	public void the_parsed_LookupBlock_response_should_have_previous_block_hash(String string) {
		// Write code here that turns the phrase above into concrete actions
		throw new io.cucumber.java.PendingException();

	}


	@Then("the parsed LookupAccountByID response should have address {string}")
	public void the_parsed_LookupAccountByID_response_should_have_address(String string) {
		throw new io.cucumber.java.PendingException();
	}


	@Then("the parsed LookupAssetByID response should have index {long}")
	public void the_parsed_LookupAssetByID_response_should_have_index(Long int1) {
		// Write code here that turns the phrase above into concrete actions
		throw new io.cucumber.java.PendingException();
	}


	@Then("the parsed SearchAccounts response should be valid on round {long} "
			+ "and the array should be of len {long} "
			+ "and the element at index {long} "
			+ "should have address {string}")
	public void the_parsed_SearchAccounts_response_should_be_valid_on_round_and_the_array_should_be_of_len_and_the_element_at_index_should_have_address(
			Long int1, Long int2, Long int3, String string) {
		// Write code here that turns the phrase above into concrete actions
		throw new io.cucumber.java.PendingException();
	}

	@Then("the parsed SearchForTransactions response should be valid on round {long} "
			+ "and the array should be of len {long} "
			+ "and the element at index {long} "
			+ "should have sender {string}")
	public void the_parsed_SearchForTransactions_response_should_be_valid_on_round_and_the_array_should_be_of_len_and_the_element_at_index_should_have_sender(
			Long int1, Long int2, Long int3, String string) {
		// Write code here that turns the phrase above into concrete actions
		throw new io.cucumber.java.PendingException();
	}


	@Then("the parsed SearchForAssets response should be valid on round {long} "
			+ "and the array should be of len {long} "
			+ "and the element at index {long} "
			+ "should have asset index {long}")
	public void the_parsed_SearchForAssets_response_should_be_valid_on_round_and_the_array_should_be_of_len_and_the_element_at_index_should_have_asset_index(
			Long int1, Long int2, Long int3, Long int4) {
		// Write code here that turns the phrase above into concrete actions
		throw new io.cucumber.java.PendingException();
	}
}
