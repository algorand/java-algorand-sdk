package com.algorand.algosdk.unit;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.unit.utils.QueryMapper;
import com.algorand.algosdk.unit.utils.TestingUtils;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.IndexerClient;
import com.algorand.algosdk.v2.client.common.Utils;
import com.algorand.algosdk.v2.client.indexer.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;

public class IndexerPaths {
    String requestUrl;
    IndexerClient indexerClient;

    @Given("mock server recording request paths")
    public void mock_server_recording_request_paths() {
        indexerClient = new IndexerClient("localhost", 1234, "");
    }

    @When("we make a Lookup Asset Balances call against asset index {long} "
            + "with limit {long} "
            + "afterAddress {string} "
            + "round {long} "
            + "currencyGreaterThan {long} "
            + "currencyLessThan {long}")
    public void we_make_a_Lookup_Asset_Balances_call_against_asset_index_with_limit_afterAddress_round_currencyGreaterThan_currencyLessThan(
            Long int1, Long int2, String string, Long int3, Long int4, Long int5) {
        try {
            LookupAssetBalances lab = indexerClient.lookupAssetBalances(int1);
            if (TestingUtils.notEmpty(int2)) {
                lab.limit(int2);
            }
            if (TestingUtils.notEmpty(int3)) {
                lab.round(int3);
            }
            if (TestingUtils.notEmpty(int4)) {
                lab.currencyGreaterThan(int4);
            }
            if (TestingUtils.notEmpty(int5)) {
                lab.currencyLessThan(int5);
            }
            requestUrl = lab.getRequestUrl(9999, "localhost");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Then("expect the path used to be {string}")
    public void expect_the_path_used_to_be(String string) {
        boolean same = TestingUtils.comparePathUrls(this.requestUrl, string, "http://localhost:9999");
        assertThat(same);
    }

    @When("we make a Lookup Asset Transactions call against asset index {long} "
            + "with NotePrefix {string} "
            + "TxType {string} "
            + "SigType {string} "
            + "txid {string} "
            + "round {long} "
            + "minRound {long} "
            + "maxRound {long} "
            + "limit {long} "
            + "beforeTime {string} "
            + "afterTime {string} "
            + "currencyGreaterThan {long} "
            + "currencyLessThan {long} "
            + "address {string} "
            + "addressRole {string} "
            + "ExcluseCloseTo {string}")
    public void we_make_a_Lookup_Asset_Transactions_call_against_asset_index_with_NotePrefix_TxType_SigType_txid_round_minRound_maxRound_limit_beforeTime_afterTime_currencyGreaterThan_currencyLessThan_address_addressRole_ExcluseCloseTo(
            Long int1, String string, String string2, String string3, String string4,
            Long int2, Long int3, Long int4, Long int5, String string5, String string6,
            Long int6, Long int7, String string7, String string8, String string9) {
        LookupAssetTransactions lat = indexerClient.lookupAssetTransactions(int1);
        if (TestingUtils.notEmpty(string)) lat.notePrefix(Encoder.decodeFromBase64(string));
        if (TestingUtils.notEmpty(string2)) lat.txType(QueryMapper.getTxType(string2));
        if (TestingUtils.notEmpty(string3)) lat.sigType(QueryMapper.getSigType(string3));
        if (TestingUtils.notEmpty(string4)) lat.txid(string4);
        if (TestingUtils.notEmpty(int2)) lat.round(int2);
        if (TestingUtils.notEmpty(int3)) lat.minRound(int3);
        if (TestingUtils.notEmpty(int4)) lat.maxRound(int4);
        if (TestingUtils.notEmpty(int5)) lat.limit(int5);
        try {
            if (TestingUtils.notEmpty(string5)) lat.beforeTime(Utils.parseDate(string5));
            if (TestingUtils.notEmpty(string6)) lat.afterTime(Utils.parseDate(string6));
            if (TestingUtils.notEmpty(int6)) lat.currencyLessThan(int6);
            if (TestingUtils.notEmpty(int7)) lat.currencyGreaterThan(int7);
            if (TestingUtils.notEmpty(string7)) lat.address(new Address(string7));
            if (TestingUtils.notEmpty(string8)) lat.addressRole(QueryMapper.getAddressRole(string8));
            if (TestingUtils.notEmpty(string9)) lat.excludeCloseTo(string9.equals("true"));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        this.requestUrl = lat.getRequestUrl(9999, "localhost");
    }

    @When("we make a Lookup Account Transactions call against account {string} "
            + "with NotePrefix {string} "
            + "TxType {string} "
            + "SigType {string} "
            + "txid {string} "
            + "round {long} "
            + "minRound {long} "
            + "maxRound {long} "
            + "limit {long} "
            + "beforeTime {string} "
            + "afterTime {string} "
            + "currencyGreaterThan {long} "
            + "currencyLessThan {long} "
            + "assetIndex {long} "
            + "addressRole {string} "
            + "ExcluseCloseTo {string}")
    public void we_make_a_Lookup_Account_Transactions_call_against_account_with_NotePrefix_TxType_SigType_txid_round_minRound_maxRound_limit_beforeTime_afterTime_currencyGreaterThan_currencyLessThan_assetIndex_addressRole_ExcluseCloseTo(
            String string, String string2, String string3, String string4,
            String string5, Long int1, Long int2, Long int3, Long int4,
            String string6, String string7, Long int5, Long int6, Long int7,
            String string8, String string9) {
        try {
            LookupAccountTransactions lat = indexerClient.lookupAccountTransactions(new Address(string));
            if (TestingUtils.notEmpty(string2)) lat.notePrefix(Encoder.decodeFromBase64(string2));
            if (TestingUtils.notEmpty(string3)) lat.txType(QueryMapper.getTxType(string3));
            if (TestingUtils.notEmpty(string4)) lat.sigType(QueryMapper.getSigType(string4));
            if (TestingUtils.notEmpty(string5)) lat.txid(string5);
            if (TestingUtils.notEmpty(int1)) lat.round(int1);
            if (TestingUtils.notEmpty(int2)) lat.minRound(int2);
            if (TestingUtils.notEmpty(int3)) lat.maxRound(int3);
            if (TestingUtils.notEmpty(int4)) lat.limit(int4);
            if (TestingUtils.notEmpty(string6)) lat.beforeTime(Utils.parseDate(string6));
            if (TestingUtils.notEmpty(string7)) lat.afterTime(Utils.parseDate(string7));
            if (TestingUtils.notEmpty(int5)) lat.currencyGreaterThan(int5);
            if (TestingUtils.notEmpty(int6)) lat.currencyLessThan(int6);
            if (TestingUtils.notEmpty(int7)) lat.assetId(int7);
            // TODO what the are addressRol and ExcluseCloseTo

            this.requestUrl = lat.getRequestUrl(9999, "localhost");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }

    @When("we make a Lookup Block call against round {long}")
    public void we_make_a_Lookup_Block_call_against_round(Long int1) {
        this.requestUrl = this.indexerClient.lookupBlock(int1).getRequestUrl(9999, "localhost");
    }

    @When("we make a Lookup Account by ID call against account {string} with round {long}")
    public void we_make_a_Lookup_Account_by_ID_call_against_account_with_round(String string, Long int1) {
        try {
            LookupAccountByID ans = this.indexerClient.lookupAccountByID(new Address(string));
            if (TestingUtils.notEmpty(int1)) ans.round(int1);
            this.requestUrl = ans.getRequestUrl(9999, "localhost");
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        }
    }

    @When("we make a Lookup Asset by ID call against asset index {long}")
    public void we_make_a_Lookup_Asset_by_ID_call_against_asset_index(Long int1) {
        this.requestUrl = this.indexerClient.lookupAssetByID(int1).getRequestUrl(9999, "localhost");
    }

    @When("we make a Search Accounts call with assetID {long} "
            + "limit {long} "
            + "currencyGreaterThan {long} "
            + "currencyLessThan {long} "
            + "and round {long}")
    public void we_make_a_Search_Accounts_call_with_assetID_limit_currencyGreaterThan_currencyLessThan_and_round(
            Long int1, Long int2, Long int3, Long int4, Long int5) {
        SearchForAccounts sfa = this.indexerClient.searchForAccounts();
        if (TestingUtils.notEmpty(int1)) sfa.assetId(int1);
        if (TestingUtils.notEmpty(int2)) sfa.limit(int2);
        if (TestingUtils.notEmpty(int3)) sfa.currencyGreaterThan(int3);
        if (TestingUtils.notEmpty(int4)) sfa.currencyLessThan(int4);
        if (TestingUtils.notEmpty(int5)) sfa.round(int5);
        this.requestUrl = sfa.getRequestUrl(9999, "localhost");
    }

    @When("we make a Search For Transactions call with account {string} "
            + "NotePrefix {string} "
            + "TxType {string} "
            + "SigType {string} "
            + "txid {string} "
            + "round {long} "
            + "minRound {long} "
            + "maxRound {long} "
            + "limit {long} "
            + "beforeTime {string} "
            + "afterTime {string} "
            + "currencyGreaterThan {long} "
            + "currencyLessThan {long} "
            + "assetIndex {long} "
            + "addressRole {string} "
            + "ExcluseCloseTo {string}")
    public void we_make_a_Search_For_Transactions_call_with_account_NotePrefix_TxType_SigType_txid_round_minRound_maxRound_limit_beforeTime_afterTime_currencyGreaterThan_currencyLessThan_assetIndex_addressRole_ExcluseCloseTo(
            String string, String string2, String string3, String string4,
            String string5, Long int1, Long int2, Long int3, Long int4,
            String string6, String string7, Long int5, Long int6, Long int7,
            String string8, String string9) {
        SearchForTransactions sft = this.indexerClient.searchForTransactions();
        try {
            if (TestingUtils.notEmpty(string)) sft.address(new Address(string));
            if (TestingUtils.notEmpty(string2)) sft.notePrefix(Encoder.decodeFromBase64(string2));
            if (TestingUtils.notEmpty(string3)) sft.txType(QueryMapper.getTxType(string3));
            if (TestingUtils.notEmpty(string4)) sft.sigType(QueryMapper.getSigType(string4));
            if (TestingUtils.notEmpty(string5)) sft.txid(string5);
            if (TestingUtils.notEmpty(int1)) sft.round(int1);
            if (TestingUtils.notEmpty(int2)) sft.minRound(int2);
            if (TestingUtils.notEmpty(int3)) sft.maxRound(int3);
            if (TestingUtils.notEmpty(int4)) sft.limit(int4);
            if (TestingUtils.notEmpty(string6)) sft.beforeTime(Utils.parseDate(string6));
            if (TestingUtils.notEmpty(string7)) sft.afterTime(Utils.parseDate(string7));
            if (TestingUtils.notEmpty(int5)) sft.currencyGreaterThan(int5);
            if (TestingUtils.notEmpty(int6)) sft.currencyLessThan(int6);
            if (TestingUtils.notEmpty(int7)) sft.assetId(int7);
            if (TestingUtils.notEmpty(string8)) sft.addressRole(QueryMapper.getAddressRole(string8));
            if (TestingUtils.notEmpty(string9)) sft.excludeCloseTo(string9.equals("true"));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        this.requestUrl = sft.getRequestUrl(9999, "localhost");
    }

    @When("we make a SearchForAssets call with limit {long} "
            + "creator {string} "
            + "name {string} "
            + "unit {string} "
            + "index {long}")
    public void we_make_a_SearchForAssets_call_with_limit_creator_name_unit_index(
            Long int1, String string, String string2, String string3, Long int2) {
        SearchForAssets sfs = this.indexerClient.searchForAssets();
        if (TestingUtils.notEmpty(int1)) sfs.limit(int1);
        if (TestingUtils.notEmpty(string)) sfs.creator(string);
        if (TestingUtils.notEmpty(string2)) sfs.name(string2);
        if (TestingUtils.notEmpty(string3)) sfs.unit(string3);
        if (TestingUtils.notEmpty(int2)) sfs.assetId(int2);
        this.requestUrl = sfs.getRequestUrl(9999, "localhost");
    }


}
