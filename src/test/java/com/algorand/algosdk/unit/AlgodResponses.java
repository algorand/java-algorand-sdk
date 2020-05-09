package com.algorand.algosdk.unit;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.unit.utils.ClientMocker;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.IndexerClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.*;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.File;
import java.io.IOException;

import static com.algorand.algosdk.unit.utils.TestingUtils.verifyResponse;
import static org.assertj.core.api.Assertions.assertThat;

public class AlgodResponses {
    AlgodClient client;
    ResponsesShared shared;

    Response<Account> accountResponse;
    Response<BlockResponse> blockResponse;
    Response<NodeStatusResponse> nodeStatusResponse;
    Response<PendingTransactionResponse> pendingTransactionResponse;
    Response<PendingTransactionsResponse> pendingTransactionsResponse;
    Response<PostTransactionsResponse> postTransactionsResponse;
    Response<SupplyResponse> supplyResponse;
    Response<TransactionParametersResponse> transactionParametersResponse;

    public AlgodResponses(ResponsesShared shared) {
        this.shared = shared;
        this.client = new AlgodClient("localhost", 123, "");
    }

    @When("we make any Pending Transaction Information call")
    public void we_make_any_Pending_Transaction_Information_call() throws Exception {
        ClientMocker.infect(client);
        pendingTransactionResponse = client.PendingTransactionInformation("yahtzee").execute();
    }

    @When("we make any Pending Transactions Information call")
    public void we_make_any_Pending_Transactions_Information_call() throws Exception {
        ClientMocker.infect(client);
        pendingTransactionsResponse = client.GetPendingTransactions().execute();
    }

    @When("we make any Send Raw Transaction call")
    public void we_make_any_Send_Raw_Transaction_call() throws Exception {
        ClientMocker.infect(client);
        postTransactionsResponse = client.RawTransaction().rawtxn("sometxn".getBytes()).execute();
    }

    @When("we make any Pending Transactions By Address call")
    public void we_make_any_Pending_Transactions_By_Address_call() throws Exception {
        ClientMocker.infect(client);
        pendingTransactionsResponse = client.GetPendingTransactionsByAddress(new Address()).execute();
    }

    @When("we make any Node Status call")
    public void we_make_any_Node_Status_call() throws Exception {
        ClientMocker.infect(client);
        nodeStatusResponse = client.GetStatus().execute();
    }

    @When("we make any Ledger Supply call")
    public void we_make_any_Ledger_Supply_call() throws Exception {
        ClientMocker.infect(client);
        supplyResponse = client.GetSupply().execute();
    }

    @When("we make any Status After Block call")
    public void we_make_any_Status_After_Block_call() throws Exception {
        ClientMocker.infect(client);
        nodeStatusResponse = client.WaitForBlock(Long.MAX_VALUE).execute();
    }

    @When("we make any Account Information call")
    public void we_make_any_Account_Information_call() throws Exception {
        ClientMocker.infect(client);
        accountResponse = client.AccountInformation(new Address()).execute();
    }

    @When("we make any Get Block call")
    public void we_make_any_Get_Block_call() throws Exception {
        ClientMocker.infect(client);
        blockResponse = client.GetBlock(-55L).execute();
    }

    @When("we make any Suggested Transaction Parameters call")
    public void we_make_any_Suggested_Transaction_Parameters_call() throws Exception {
        ClientMocker.infect(client);
        transactionParametersResponse = client.TransactionParams().execute();
    }

    @Then("the parsed Pending Transaction Information response should have sender {string}")
    public void the_parsed_Pending_Transaction_Information_response_should_have_sender(String string) throws IOException {
        verifyResponse(pendingTransactionResponse, shared.bodyFile);
    }

    @Then("the parsed Pending Transactions Information response should contain an array of len {int} and element number {int} should have sender {string}")
    public void the_parsed_Pending_Transactions_Information_response_should_contain_an_array_of_len_and_element_number_should_have_sender(Integer int1, Integer int2, String string) throws IOException {
        verifyResponse(pendingTransactionsResponse, shared.bodyFile);
    }

    @Then("the parsed Pending Transactions By Address response should contain an array of len {int} and element number {int} should have sender {string}")
    public void the_parsed_Pending_Transactions_By_Address_response_should_contain_an_array_of_len_and_element_number_should_have_sender(Integer int1, Integer int2, String string) throws IOException {
        verifyResponse(pendingTransactionsResponse, shared.bodyFile);
    }

    @Then("the parsed Node Status response should have a last round of {int}")
    public void the_parsed_Node_Status_response_should_have_a_last_round_of(Integer int1) throws IOException {
        verifyResponse(nodeStatusResponse, shared.bodyFile);
    }

    @Then("the parsed Ledger Supply response should have totalMoney {int} onlineMoney {int} on round {int}")
    public void the_parsed_Ledger_Supply_response_should_have_totalMoney_onlineMoney_on_round(Integer int1, Integer int2, Integer int3) throws IOException {
        verifyResponse(supplyResponse, shared.bodyFile);
    }

    @Then("the parsed Status After Block response should have a last round of {int}")
    public void the_parsed_Status_After_Block_response_should_have_a_last_round_of(Integer int1) throws IOException {
        verifyResponse(nodeStatusResponse, shared.bodyFile);
    }

    @Then("the parsed Account Information response should have address {string}")
    public void the_parsed_Account_Information_response_should_have_address(String string) throws IOException {
        verifyResponse(accountResponse, shared.bodyFile);
    }

    @Then("the parsed Get Block response should have rewards pool {string}")
    public void the_parsed_Get_Block_response_should_have_rewards_pool(String string) throws IOException {
        verifyResponse(blockResponse, shared.bodyFile);
    }

    @Then("the parsed Suggested Transaction Parameters response should have first round valid of {int}")
    public void the_parsed_Suggested_Transaction_Parameters_response_should_have_first_round_valid_of(Integer int1) throws IOException {
        verifyResponse(transactionParametersResponse, shared.bodyFile);
    }

    @Then("the parsed Send Raw Transaction response should have txid {string}")
    public void the_parsed_Send_Raw_Transaction_response_should_have_txid(String txid) throws IOException {
        verifyResponse(postTransactionsResponse, shared.bodyFile);
    }
}
