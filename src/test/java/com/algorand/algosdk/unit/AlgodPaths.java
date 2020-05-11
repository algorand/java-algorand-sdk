package com.algorand.algosdk.unit;

import com.algorand.algosdk.v2.client.algod.PendingTransactionInformation;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import io.cucumber.java.en.When;

public class AlgodPaths {
    String requestUrl;
    AlgodClient algodClient = new AlgodClient("localhost", 1234, "");
    PathsShared ps;

    public AlgodPaths(PathsShared ps) {
        this.ps = ps;
    }

    @When("we make a Pending Transaction Information against txid {string} with format {string}")
    public void we_make_a_Pending_Transaction_Information_against_txid_with_format(String txid, String format) {
        PendingTransactionInformation pti = algodClient.PendingTransactionInformation(txid);

        ps.requestUrl = pti.getRequestUrl(123, "localhost");
    }

    @When("we make a Pending Transaction Information with max {int} and format {string}")
    public void we_make_a_Pending_Transaction_Information_with_max_and_format(Integer int1, String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("we make a Pending Transactions By Address call against account {string} and max {int} and format {string}")
    public void we_make_a_Pending_Transactions_By_Address_call_against_account_and_max_and_format(String string, Integer int1, String string2) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("we make a Status after Block call with round {int}")
    public void we_make_a_Status_after_Block_call_with_round(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("we make an Account Information call against account {string}")
    public void we_make_an_Account_Information_call_against_account(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("we make a Get Block call against block number {int} with format {string}")
    public void we_make_a_Get_Block_call_against_block_number_with_format(Integer int1, String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
}
