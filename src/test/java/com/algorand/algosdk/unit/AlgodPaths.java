package com.algorand.algosdk.unit;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.unit.utils.TestingUtils;
import com.algorand.algosdk.v2.client.algod.*;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import io.cucumber.java.en.When;

import java.security.NoSuchAlgorithmException;

public class AlgodPaths {
    AlgodClient algodClient = new AlgodClient("localhost", 1234, "");
    PathsShared ps;

    public AlgodPaths(PathsShared ps) {
        this.ps = ps;
    }

    @When("we make a Pending Transaction Information against txid {string} with format {string}")
    public void we_make_a_Pending_Transaction_Information_against_txid_with_format(String txid, String format) {
        PendingTransactionInformation q = algodClient.PendingTransactionInformation(txid);

        ps.requestUrl = q.getRequestUrl(123, "localhost");
    }

    @When("we make a Pending Transaction Information with max {long} and format {string}")
    public void we_make_a_Pending_Transaction_Information_with_max_and_format(Long max, String format) {
        GetPendingTransactions q = algodClient.GetPendingTransactions();

        if (TestingUtils.notEmpty(max)) q.max(max);

        ps.requestUrl = q.getRequestUrl(123, "localhost");
    }

    @When("we make a Pending Transactions By Address call against account {string} and max {long} and format {string}")
    public void we_make_a_Pending_Transactions_By_Address_call_against_account_and_max_and_format(String txid, Long max, String format) throws NoSuchAlgorithmException {
        GetPendingTransactionsByAddress q = algodClient.GetPendingTransactionsByAddress(new Address(txid));

        if (TestingUtils.notEmpty(max)) q.max(max);

        ps.requestUrl = q.getRequestUrl(123, "localhost");
    }

    @When("we make a Status after Block call with round {long}")
    public void we_make_a_Status_after_Block_call_with_round(Long round) {
        WaitForBlock q = algodClient.WaitForBlock(round);

        ps.requestUrl = q.getRequestUrl(123, "localhost");
    }

    @When("we make an Account Information call against account {string}")
    public void we_make_an_Account_Information_call_against_account(String account) throws NoSuchAlgorithmException {
        AccountInformation q = algodClient.AccountInformation(new Address(account));

        ps.requestUrl = q.getRequestUrl(123, "localhost");
    }

    @When("we make a Get Block call against block number {long} with format {string}")
    public void we_make_a_Get_Block_call_against_block_number_with_format(Long round, String format) {
        GetBlock q = algodClient.GetBlock(round);

        ps.requestUrl = q.getRequestUrl(123, "localhost");
    }
}
