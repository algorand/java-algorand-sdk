package com.algorand.algosdk.unit;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.unit.utils.TestingUtils;
import com.algorand.algosdk.v2.client.algod.AccountInformation;
import com.algorand.algosdk.v2.client.algod.GetApplicationBoxes;
import com.algorand.algosdk.v2.client.algod.GetPendingTransactions;
import com.algorand.algosdk.v2.client.algod.GetPendingTransactionsByAddress;
import com.algorand.algosdk.v2.client.algod.GetTransactionProof;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.model.Enums;
import io.cucumber.java.en.When;

import java.security.NoSuchAlgorithmException;

public class AlgodPaths {
    AlgodClient algodClient = new AlgodClient("localhost", 1234, "");
    PathsShared ps;

    public AlgodPaths(PathsShared ps) {
        this.ps = ps;
    }

    @When("we make a Pending Transaction Information against txid {string} with format {string}")
    public void pendingTransactionInformation(String txid, String format) {
        ps.q = algodClient.PendingTransactionInformation(txid);
    }

    @When("we make a Pending Transaction Information with max {long} and format {string}")
    public void getPendingTransactions(Long max, String format) {
        GetPendingTransactions q = algodClient.GetPendingTransactions();

        if (TestingUtils.notEmpty(max)) q.max(max);

        ps.q = q;
    }

    @When("we make a Pending Transactions By Address call against account {string} and max {long} and format {string}")
    public void getPendingTransactionsByAddress(String txid, Long max, String format) throws NoSuchAlgorithmException {
        GetPendingTransactionsByAddress q = algodClient.GetPendingTransactionsByAddress(new Address(txid));

        if (TestingUtils.notEmpty(max)) q.max(max);

        ps.q = q;
    }

    @When("we make a Status after Block call with round {long}")
    public void waitForBlock(Long round) {
        ps.q = algodClient.WaitForBlock(round);
    }

    @When("we make an Account Information call against account {string}")
    public void accountInformation(String account) throws NoSuchAlgorithmException {
        ps.q = algodClient.AccountInformation(new Address(account));
    }

    @When("we make a Get Block call against block number {long} with format {string}")
    public void getBlock(Long round, String format) {
        ps.q = algodClient.GetBlock(round);
    }

    @When("we make a GetAssetByID call for assetID {long}")
    public void getAssetByID(Long id) {
        ps.q = algodClient.GetAssetByID(id);
    }

    @When("we make a GetApplicationByID call for applicationID {long}")
    public void getApplicationByID(Long id) {
        ps.q = algodClient.GetApplicationByID(id);
    }

    @When("we make an Account Application Information call against account {string} applicationID {int}")
    public void accountApplicationInformation(String string, Integer int1) throws NoSuchAlgorithmException {
        ps.q = algodClient.AccountApplicationInformation(new Address(string), (long) int1.intValue());
    }

    @When("we make an Account Asset Information call against account {string} assetID {int}")
    public void accountAssetInformation(String string, Integer int1) throws NoSuchAlgorithmException {
        ps.q = algodClient.AccountAssetInformation(new Address(string), (long) int1.intValue());
    }

    @When("we make an Account Information call against account {string} with exclude {string}")
    public void accountInformation(String string, String string2) throws NoSuchAlgorithmException {
        AccountInformation aiq = algodClient.AccountInformation(new Address(string));
        if (TestingUtils.notEmpty(string2)) aiq.exclude(Enums.Exclude.forValue(string2));
        ps.q = aiq;
    }

    @When("we make a GetApplicationBoxByName call for applicationID {long} with encoded box name {string}")
    public void getBoxByName(Long appID, String encodedBoxName) {
        ps.q = algodClient.GetApplicationBoxByName(appID).name(encodedBoxName);
    }

    @When("we make a GetApplicationBoxes call for applicationID {long} with max {long}")
    public void getBoxes(Long appId, Long max) {
        GetApplicationBoxes q = algodClient.GetApplicationBoxes(appId);

        if (TestingUtils.notEmpty(max)) q.max(max);

        ps.q = q;
    }

    @When("we make a GetTransactionProof call for round {long} txid {string} and hashtype {string}")
    public void getTransactionProof(Long round, String txid, String hashType) {
        GetTransactionProof gtp = algodClient.GetTransactionProof(round, txid);
        if (TestingUtils.notEmpty(hashType)) gtp.hashtype(Enums.Hashtype.forValue(hashType));
        ps.q = gtp;
    }

    @When("we make a GetLightBlockHeaderProof call for round {long}")
    public void getLightBlockHeaderProof(Long round) {
        ps.q = algodClient.GetLightBlockHeaderProof(round);
    }

    @When("we make a GetStateProof call for round {long}")
    public void getStateProof(Long round) {
        ps.q = algodClient.GetStateProof(round);
    }

    @When("we make a Lookup Block Hash call against round {long}")
    public void getBlockHash(Long round) {
        ps.q = algodClient.GetBlockHash(round);
    }

    @When("we make a GetBlockTimeStampOffset call")
    public void getBlockTimestampOffset() {
        ps.q = algodClient.;
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

}
