package com.algorand.algosdk.unit;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.unit.utils.TestingUtils;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.IndexerClient;
import com.algorand.algosdk.v2.client.common.Utils;
import com.algorand.algosdk.v2.client.indexer.*;
import com.algorand.algosdk.v2.client.model.Enums;

import io.cucumber.java.en.When;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;

public class IndexerPaths {
    IndexerClient indexerClient = new IndexerClient("localhost", 1234, "");;
    PathsShared ps;

    public IndexerPaths(PathsShared ps) {
        this.ps = ps;
    }

    @When("we make a Lookup Asset Balances call against asset index {long} "
            + "with limit {long} "
            + "afterAddress {string} "
            + "currencyGreaterThan {long} "
            + "currencyLessThan {long}")
    public void lookupAssetBalances(Long assetId, Long limit, String afterAddress, Long currenGT, Long currencyLT) {
        LookupAssetBalances q = indexerClient.lookupAssetBalances(assetId);
        if (TestingUtils.notEmpty(limit)) q.limit(limit);
        if (TestingUtils.notEmpty(currenGT)) q.currencyGreaterThan(currenGT);
        if (TestingUtils.notEmpty(currencyLT)) q.currencyLessThan(currencyLT);
        ps.q = q;
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
    public void oldLookupAssetTransactions(
            Long assetId, String notePrefix, String txType, String sigType, String txid,
            Long round, Long minRound, Long maxRound, Long limit, String beforeTime, String afterTime,
            Long currencyGreaterThan, Long currencyLessThan, String address, String addressRole, String excludeCloseTo) throws ParseException, NoSuchAlgorithmException {
        lookupAssetTransactions(assetId, notePrefix, txType, sigType, txid, round, minRound, maxRound,
                limit, beforeTime, afterTime, currencyGreaterThan, currencyLessThan, address, addressRole,
                excludeCloseTo, "");
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
            + "assetIndex {long}")
    public void oldLookupAccountTransactions(
            String account, String notePrefix, String txType, String sigType,
            String txid, Long round, Long minRound, Long maxRound, Long limit,
            String beforeTime, String afterTime, Long currencyGT, Long currencyLT, Long assetId) throws NoSuchAlgorithmException, ParseException {
        lookupAccountTransactions(account, notePrefix, txType, sigType, txid, round, minRound, maxRound,
                limit, beforeTime, afterTime, currencyGT, currencyLT, assetId, "");
    }

    @When("we make a Lookup Block call against round {long}")
    public void lookupBlock(Long round) {
        ps.q = this.indexerClient.lookupBlock(round);
    }

    @When("we make a Lookup Account by ID call against account {string} with round {long}")
    public void lookupAccountByID(String string, Long int1) throws NoSuchAlgorithmException {
        LookupAccountByID q = this.indexerClient.lookupAccountByID(new Address(string));
        if (TestingUtils.notEmpty(int1)) q.round(int1);
        ps.q = q;
    }

    @When("we make a Lookup Asset by ID call against asset index {long}")
    public void lookupAssetByID(Long id) {
        ps.q = this.indexerClient.lookupAssetByID(id);
    }

    @When("we make a Search Accounts call with assetID {long} "
            + "limit {long} "
            + "currencyGreaterThan {long} "
            + "currencyLessThan {long} "
            + "and round {long}")
    public void oldSearchAccounts(
            Long assetId, Long limit, Long currencyGT, Long currencyLT, Long round) throws NoSuchAlgorithmException {
        searchAccounts(assetId, limit, currencyGT, currencyLT, round, "");
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
    public void oldSearchForTransactions(
            String address, String notePrefix, String txType, String sigType,
            String txid, Long round, Long minRound, Long maxRound, Long limit,
            String beforeTime, String afterTime, Long currencyGT, Long currencyLT, Long assetID,
            String addressRole, String excludeCloseTo) throws ParseException, NoSuchAlgorithmException {
        searchForTransactions(address, notePrefix, txType, sigType, txid, round, minRound,
                maxRound, limit, beforeTime, afterTime, currencyGT, currencyLT, assetID,
                addressRole, excludeCloseTo, "");
    }

    @When("we make a SearchForAssets call with limit {long} "
            + "creator {string} "
            + "name {string} "
            + "unit {string} "
            + "index {long}")
    public void searchForAssets(Long limit, String creator, String name, String unit, Long id) {
        SearchForAssets q = this.indexerClient.searchForAssets();
        if (TestingUtils.notEmpty(limit)) q.limit(limit);
        if (TestingUtils.notEmpty(creator)) q.creator(creator);
        if (TestingUtils.notEmpty(name)) q.name(name);
        if (TestingUtils.notEmpty(unit)) q.unit(unit);
        if (TestingUtils.notEmpty(id)) q.assetId(id);
        ps.q = q;
    }

    @When("we make a Lookup Asset Transactions call against asset index {long} with NotePrefix {string} TxType {string} SigType {string} txid {string} round {long} minRound {long} maxRound {long} limit {long} beforeTime {string} afterTime {string} currencyGreaterThan {long} currencyLessThan {long} address {string} addressRole {string} ExcluseCloseTo {string} RekeyTo {string}")
    public void lookupAssetTransactions(Long assetId, String notePrefix, String txType, String sigType, String txid, Long round, Long minRound, Long maxRound, Long limit, String beforeTime, String afterTime, Long currencyGT, Long currencyLT, String address, String addressRole, String excludeCloseTo, String rekeyTo) throws ParseException, NoSuchAlgorithmException {
        LookupAssetTransactions q = this.indexerClient.lookupAssetTransactions(assetId);
        if (TestingUtils.notEmpty(address)) q.address(new Address(address));
        if (TestingUtils.notEmpty(notePrefix)) q.notePrefix(Encoder.decodeFromBase64(notePrefix));
        if (TestingUtils.notEmpty(txType)) q.txType(Enums.TxType.forValue(txType));
        if (TestingUtils.notEmpty(sigType)) q.sigType(Enums.SigType.forValue(sigType));
        if (TestingUtils.notEmpty(txid)) q.txid(txid);
        if (TestingUtils.notEmpty(round)) q.round(round);
        if (TestingUtils.notEmpty(minRound)) q.minRound(minRound);
        if (TestingUtils.notEmpty(maxRound)) q.maxRound(maxRound);
        if (TestingUtils.notEmpty(limit)) q.limit(limit);
        if (TestingUtils.notEmpty(beforeTime)) q.beforeTime(Utils.parseDate(beforeTime));
        if (TestingUtils.notEmpty(afterTime)) q.afterTime(Utils.parseDate(afterTime));
        if (TestingUtils.notEmpty(currencyGT)) q.currencyGreaterThan(currencyGT);
        if (TestingUtils.notEmpty(currencyLT)) q.currencyLessThan(currencyLT);
        if (TestingUtils.notEmpty(addressRole)) q.addressRole(Enums.AddressRole.forValue(addressRole));
        if (TestingUtils.notEmpty(excludeCloseTo)) q.excludeCloseTo(excludeCloseTo.equals("true"));
        if (TestingUtils.notEmpty(rekeyTo)) q.rekeyTo(rekeyTo.equals("true"));
        ps.q = q;
    }

    @When("we make a Lookup Account Transactions call against account {string} with NotePrefix {string} TxType {string} SigType {string} txid {string} round {long} minRound {long} maxRound {long} limit {long} beforeTime {string} afterTime {string} currencyGreaterThan {long} currencyLessThan {long} assetIndex {long} rekeyTo {string}")
    public void lookupAccountTransactions(String account, String notePrefix, String txType, String sigType, String txid, Long round, Long minRound, Long maxRound, Long limit, String beforeTime, String afterTime, Long currencyGT, Long currencyLT, Long assetId, String rekeyTo) throws NoSuchAlgorithmException, ParseException {
        LookupAccountTransactions q = this.indexerClient.lookupAccountTransactions(new Address(account));
        if (TestingUtils.notEmpty(notePrefix)) q.notePrefix(Encoder.decodeFromBase64(notePrefix));
        if (TestingUtils.notEmpty(txType)) q.txType(Enums.TxType.forValue(txType));
        if (TestingUtils.notEmpty(sigType)) q.sigType(Enums.SigType.forValue(sigType));
        if (TestingUtils.notEmpty(txid)) q.txid(txid);
        if (TestingUtils.notEmpty(round)) q.round(round);
        if (TestingUtils.notEmpty(minRound)) q.minRound(minRound);
        if (TestingUtils.notEmpty(maxRound)) q.maxRound(maxRound);
        if (TestingUtils.notEmpty(limit)) q.limit(limit);
        if (TestingUtils.notEmpty(beforeTime)) q.beforeTime(Utils.parseDate(beforeTime));
        if (TestingUtils.notEmpty(afterTime)) q.afterTime(Utils.parseDate(afterTime));
        if (TestingUtils.notEmpty(currencyGT)) q.currencyGreaterThan(currencyGT);
        if (TestingUtils.notEmpty(currencyLT)) q.currencyLessThan(currencyLT);
        if (TestingUtils.notEmpty(assetId)) q.assetId(assetId);
        if (TestingUtils.notEmpty(rekeyTo)) q.rekeyTo(rekeyTo.equals("true"));
        ps.q = q;
    }

    @When("we make a Search Accounts call with assetID {long} limit {long} currencyGreaterThan {long} currencyLessThan {long} round {long} and authenticating address {string}")
    public void searchAccounts(Long assetId, Long limit, Long currencyGT, Long currencyLT, Long round, String authAddr) throws NoSuchAlgorithmException {
        SearchForAccounts q = this.indexerClient.searchForAccounts();
        if (TestingUtils.notEmpty(assetId)) q.assetId(assetId);
        if (TestingUtils.notEmpty(limit)) q.limit(limit);
        if (TestingUtils.notEmpty(currencyGT)) q.currencyGreaterThan(currencyGT);
        if (TestingUtils.notEmpty(currencyLT)) q.currencyLessThan(currencyLT);
        if (TestingUtils.notEmpty(round)) q.round(round);
        if (TestingUtils.notEmpty(authAddr)) q.authAddr(new Address(authAddr));
        ps.q = q;
    }

    @When("we make a Search For Transactions call with account {string} NotePrefix {string} TxType {string} SigType {string} txid {string} round {long} minRound {long} maxRound {long} limit {long} beforeTime {string} afterTime {string} currencyGreaterThan {long} currencyLessThan {long} assetIndex {long} addressRole {string} ExcluseCloseTo {string} rekeyTo {string}")
    public void searchForTransactions(String address, String notePrefix, String txType, String sigType,
            String txid, Long round, Long minRound, Long maxRound, Long limit,
            String beforeTime, String afterTime, Long currencyGT, Long currencyLT, Long assetID,
            String addressRole, String excludeCloseTo, String rekeyTo) throws ParseException, NoSuchAlgorithmException {

        SearchForTransactions q = this.indexerClient.searchForTransactions();
        if (TestingUtils.notEmpty(address)) q.address(new Address(address));
        if (TestingUtils.notEmpty(notePrefix)) q.notePrefix(Encoder.decodeFromBase64(notePrefix));
        if (TestingUtils.notEmpty(txType)) q.txType(Enums.TxType.forValue(txType));
        if (TestingUtils.notEmpty(sigType)) q.sigType(Enums.SigType.forValue(sigType));
        if (TestingUtils.notEmpty(txid)) q.txid(txid);
        if (TestingUtils.notEmpty(round)) q.round(round);
        if (TestingUtils.notEmpty(minRound)) q.minRound(minRound);
        if (TestingUtils.notEmpty(maxRound)) q.maxRound(maxRound);
        if (TestingUtils.notEmpty(limit)) q.limit(limit);
        if (TestingUtils.notEmpty(beforeTime)) q.beforeTime(Utils.parseDate(beforeTime));
        if (TestingUtils.notEmpty(afterTime)) q.afterTime(Utils.parseDate(afterTime));
        if (TestingUtils.notEmpty(currencyGT)) q.currencyGreaterThan(currencyGT);
        if (TestingUtils.notEmpty(currencyLT)) q.currencyLessThan(currencyLT);
        if (TestingUtils.notEmpty(assetID)) q.assetId(assetID);
        if (TestingUtils.notEmpty(addressRole)) q.addressRole(Enums.AddressRole.forValue(addressRole));
        if (TestingUtils.notEmpty(excludeCloseTo)) q.excludeCloseTo(excludeCloseTo.equals("true"));
        if (TestingUtils.notEmpty(rekeyTo)) q.rekeyTo(rekeyTo.equals("true"));
        ps.q = q;
    }

    @When("we make a SearchForApplications call with applicationID {long}")
    public void searchForApplications(Long appId) {
        SearchForApplications q = this.indexerClient.searchForApplications();
        q.applicationId(appId);
        ps.q = q;
    }

    @When("we make a LookupApplications call with applicationID {long}")
    public void lookupApplication(Long appId) {
        ps.q = this.indexerClient.lookupApplicationByID(appId);
    }

    @When("we make a LookupApplicationLogsByID call with applicationID {long} limit {long} minRound {long} maxRound {long} nextToken {string} sender {string} and txID {string}")
    public void lookupApplicationLogs(Long appID, Long limit, Long minRound, Long maxRound, String nextToken, String sender, String txID) throws NoSuchAlgorithmException {
        LookupApplicationLogsByID q = this.indexerClient.lookupApplicationLogsByID(appID);
        if (TestingUtils.notEmpty(limit)) q.limit(limit);
        if (TestingUtils.notEmpty(maxRound)) q.maxRound(maxRound);
        if (TestingUtils.notEmpty(minRound)) q.minRound(minRound);
        if (TestingUtils.notEmpty(nextToken)) q.next(nextToken);
        if (TestingUtils.notEmpty(sender)) q.senderAddress(new Address(sender));
        if (TestingUtils.notEmpty(txID)) q.txid(txID);
        ps.q = q;
    }

    @When("we make a LookupAccountAppLocalStates call with accountID {string} applicationID {int} includeAll {string} limit {int} next {string}")
    public void lookupAccountAppLocalStates(String string, Integer int1, String string2, Integer int2, String string3) throws NoSuchAlgorithmException {
        LookupAccountAppLocalStates q = this.indexerClient.lookupAccountAppLocalStates(new Address(string));
        if (string2.contentEquals("true")) q.includeAll(true);
        if (TestingUtils.notEmpty((long)int1)) q.applicationId((long)int1);
        if (TestingUtils.notEmpty((long)int2)) q.limit((long)int2);
        if (TestingUtils.notEmpty(string3)) q.next(string3);
        ps.q = q;
    }

    @When("we make a LookupAccountAssets call with accountID {string} assetID {int} includeAll {string} limit {int} next {string}")
    public void lookupAccountAssets(String string, Integer int1, String string2, Integer int2, String string3) throws NoSuchAlgorithmException {
        LookupAccountAssets q = null;
        q = this.indexerClient.lookupAccountAssets(new Address(string));
        if (TestingUtils.notEmpty((long)int1)) q.assetId((long)int1);
        if (string2.contentEquals("true")) q.includeAll(true);
        if (TestingUtils.notEmpty((long)int2)) q.limit((long)int2);
        if (string3.compareTo("") != 0) 
            q.next(string3);
        ps.q = q;
    }

    @When("we make a Lookup Account by ID call against account {string} with exclude {string}")
    public void lookupAccountByID(String string, String string2) throws NoSuchAlgorithmException {
        LookupAccountByID q = null;
        q = this.indexerClient.lookupAccountByID(new Address(string));

        if (TestingUtils.notEmpty(string2)) {
            ArrayList<Enums.Exclude> excludes = new ArrayList<Enums.Exclude>();
            for (String excld : string2.split(",")) {
                excludes.add(Enums.Exclude.forValue(excld));
            }
            q.exclude(excludes);
        }
        ps.q = q;
    }

    @When("we make a LookupAccountCreatedApplications call with accountID {string} applicationID {int} includeAll {string} limit {int} next {string}")
    public void lookupAccountCreatedApplications(String string, Integer int1, String string2, Integer int2, String string3) throws NoSuchAlgorithmException {
        LookupAccountCreatedApplications q = null;
        q = this.indexerClient.lookupAccountCreatedApplications(new Address(string));
        if (TestingUtils.notEmpty((long)int1)) q.applicationId((long)int1);
        if (string2.contentEquals("true")) q.includeAll(true);
        if (TestingUtils.notEmpty((long)int2)) q.limit((long)int2);
        if (TestingUtils.notEmpty(string3)) q.next(string3);
        ps.q = q;
    }

    @When("we make a LookupAccountCreatedAssets call with accountID {string} assetID {int} includeAll {string} limit {int} next {string}")
    public void lookupAccountCreatedAssets(String string, Integer int1, String string2, Integer int2, String string3) throws NoSuchAlgorithmException {
        LookupAccountCreatedAssets q = null;
        q = this.indexerClient.lookupAccountCreatedAssets(new Address(string));
        if (TestingUtils.notEmpty((long)int1)) q.assetId((long)int1);
        if (string2.contentEquals("true")) q.includeAll(true);
        if (TestingUtils.notEmpty((long)int2)) q.limit((long)int2);
        if (TestingUtils.notEmpty(string3)) q.next(string3);
        ps.q = q;
    }

    @When("we make a Search Accounts call with exclude {string}")
    public void searchForAccounts(String string) {
        SearchForAccounts q = this.indexerClient.searchForAccounts();
        if (TestingUtils.notEmpty(string)) {
            ArrayList<Enums.Exclude> excludes = new ArrayList<Enums.Exclude>();
            for (String excld : string.split(",")) {
                excludes.add(Enums.Exclude.forValue(excld));
            }
            q.exclude(excludes);
        }
        ps.q = q;
    }

    @When("we make a SearchForApplications call with creator {string}")
    public void searchForApplications(String string) {
        SearchForApplications q = this.indexerClient.searchForApplications();
        if (TestingUtils.notEmpty(string)) q.creator(string);
        ps.q = q;
    }


    @When("we make a Lookup Block call against round {long} and header {string}")
    public void anyBlockLookupCall(Long round, String headerOnly) {
        LookupBlock q = this.indexerClient.lookupBlock(round);
        if (headerOnly.contentEquals("true")) q.headerOnly(true);
        ps.q = q;
    }
}
