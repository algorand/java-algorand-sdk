package com.algorand.algosdk.v2.client.common;

import com.algorand.algosdk.v2.client.indexer.MakeHealthCheck;
import com.algorand.algosdk.v2.client.indexer.SearchForAccounts;
import com.algorand.algosdk.v2.client.indexer.LookupAccountByID;
import com.algorand.algosdk.v2.client.indexer.LookupAccountAssets;
import com.algorand.algosdk.v2.client.indexer.LookupAccountCreatedAssets;
import com.algorand.algosdk.v2.client.indexer.LookupAccountAppLocalStates;
import com.algorand.algosdk.v2.client.indexer.LookupAccountCreatedApplications;
import com.algorand.algosdk.v2.client.indexer.LookupAccountTransactions;
import com.algorand.algosdk.v2.client.indexer.SearchForApplications;
import com.algorand.algosdk.v2.client.indexer.LookupApplicationByID;
import com.algorand.algosdk.v2.client.indexer.SearchForApplicationBoxes;
import com.algorand.algosdk.v2.client.indexer.LookupApplicationBoxByIDAndName;
import com.algorand.algosdk.v2.client.indexer.LookupApplicationLogsByID;
import com.algorand.algosdk.v2.client.indexer.SearchForAssets;
import com.algorand.algosdk.v2.client.indexer.LookupAssetByID;
import com.algorand.algosdk.v2.client.indexer.LookupAssetBalances;
import com.algorand.algosdk.v2.client.indexer.LookupAssetTransactions;
import com.algorand.algosdk.v2.client.indexer.LookupBlock;
import com.algorand.algosdk.v2.client.indexer.LookupTransaction;
import com.algorand.algosdk.v2.client.indexer.SearchForTransactions;
import com.algorand.algosdk.crypto.Address;

public class IndexerClient extends Client {

    /**
     * Construct an IndexerClient for communicating with the REST API.
     * @param host using a URI format. If the scheme is not supplied the client will use HTTP.
     * @param port REST server port.
     * @param token authentication token.
     */
    public IndexerClient(String host, int port, String token) {
        super(host, port, token, "X-Indexer-API-Token");
    }

    /**
     * Construct an IndexerClient for communicating with the REST API.
     * @param host using a URI format. If the scheme is not supplied the client will use HTTP.
     * @param port REST server port.
     */
    public IndexerClient(String host, int port) {
        super(host, port, "", "X-Indexer-API-Token");
    }

    /**
     * Construct an IndexerClient with custom token key for communicating with the REST API.
     * @param host using a URI format. If the scheme is not supplied the client will use HTTP.
     * @param port REST server port.
     * @param token authentication token.
     * @param tokenKey authentication token key.
     */
    public IndexerClient(String host, int port, String token, String tokenKey) {
        super(host, port, token, tokenKey);
    }

    /**
     * Returns 200 if healthy.
     * /health
     */
    public MakeHealthCheck makeHealthCheck() {
        return new MakeHealthCheck((Client) this);
    }

    /**
     * Search for accounts.
     * /v2/accounts
     */
    public SearchForAccounts searchForAccounts() {
        return new SearchForAccounts((Client) this);
    }

    /**
     * Lookup account information.
     * /v2/accounts/{account-id}
     */
    public LookupAccountByID lookupAccountByID(Address accountId) {
        return new LookupAccountByID((Client) this, accountId);
    }

    /**
     * Lookup an account's asset holdings, optionally for a specific ID.
     * /v2/accounts/{account-id}/assets
     */
    public LookupAccountAssets lookupAccountAssets(Address accountId) {
        return new LookupAccountAssets((Client) this, accountId);
    }

    /**
     * Lookup an account's created asset parameters, optionally for a specific ID.
     * /v2/accounts/{account-id}/created-assets
     */
    public LookupAccountCreatedAssets lookupAccountCreatedAssets(Address accountId) {
        return new LookupAccountCreatedAssets((Client) this, accountId);
    }

    /**
     * Lookup an account's asset holdings, optionally for a specific ID.
     * /v2/accounts/{account-id}/apps-local-state
     */
    public LookupAccountAppLocalStates lookupAccountAppLocalStates(Address accountId) {
        return new LookupAccountAppLocalStates((Client) this, accountId);
    }

    /**
     * Lookup an account's created application parameters, optionally for a specific
     * ID.
     * /v2/accounts/{account-id}/created-applications
     */
    public LookupAccountCreatedApplications lookupAccountCreatedApplications(Address accountId) {
        return new LookupAccountCreatedApplications((Client) this, accountId);
    }

    /**
     * Lookup account transactions. Transactions are returned newest to oldest.
     * /v2/accounts/{account-id}/transactions
     */
    public LookupAccountTransactions lookupAccountTransactions(Address accountId) {
        return new LookupAccountTransactions((Client) this, accountId);
    }

    /**
     * Search for applications
     * /v2/applications
     */
    public SearchForApplications searchForApplications() {
        return new SearchForApplications((Client) this);
    }

    /**
     * Lookup application.
     * /v2/applications/{application-id}
     */
    public LookupApplicationByID lookupApplicationByID(Long applicationId) {
        return new LookupApplicationByID((Client) this, applicationId);
    }

    /**
     * Given an application ID, returns the box names of that application sorted
     * lexicographically.
     * /v2/applications/{application-id}/boxes
     */
    public SearchForApplicationBoxes searchForApplicationBoxes(Long applicationId) {
        return new SearchForApplicationBoxes((Client) this, applicationId);
    }

    /**
     * Given an application ID and box name, returns base64 encoded box name and value.
     * Box names must be in the goal app call arg form 'encoding:value'. For ints, use
     * the form 'int:1234'. For raw bytes, encode base 64 and use 'b64' prefix as in
     * 'b64:A=='. For printable strings, use the form 'str:hello'. For addresses, use
     * the form 'addr:XYZ...'.
     * /v2/applications/{application-id}/box
     */
    public LookupApplicationBoxByIDAndName lookupApplicationBoxByIDAndName(Long applicationId) {
        return new LookupApplicationBoxByIDAndName((Client) this, applicationId);
    }

    /**
     * Lookup application logs.
     * /v2/applications/{application-id}/logs
     */
    public LookupApplicationLogsByID lookupApplicationLogsByID(Long applicationId) {
        return new LookupApplicationLogsByID((Client) this, applicationId);
    }

    /**
     * Search for assets.
     * /v2/assets
     */
    public SearchForAssets searchForAssets() {
        return new SearchForAssets((Client) this);
    }

    /**
     * Lookup asset information.
     * /v2/assets/{asset-id}
     */
    public LookupAssetByID lookupAssetByID(Long assetId) {
        return new LookupAssetByID((Client) this, assetId);
    }

    /**
     * Lookup the list of accounts who hold this asset
     * /v2/assets/{asset-id}/balances
     */
    public LookupAssetBalances lookupAssetBalances(Long assetId) {
        return new LookupAssetBalances((Client) this, assetId);
    }

    /**
     * Lookup transactions for an asset. Transactions are returned oldest to newest.
     * /v2/assets/{asset-id}/transactions
     */
    public LookupAssetTransactions lookupAssetTransactions(Long assetId) {
        return new LookupAssetTransactions((Client) this, assetId);
    }

    /**
     * Lookup block.
     * /v2/blocks/{round-number}
     */
    public LookupBlock lookupBlock(Long roundNumber) {
        return new LookupBlock((Client) this, roundNumber);
    }

    /**
     * Lookup a single transaction.
     * /v2/transactions/{txid}
     */
    public LookupTransaction lookupTransaction(String txid) {
        return new LookupTransaction((Client) this, txid);
    }

    /**
     * Search for transactions. Transactions are returned oldest to newest unless the
     * address parameter is used, in which case results are returned newest to oldest.
     * /v2/transactions
     */
    public SearchForTransactions searchForTransactions() {
        return new SearchForTransactions((Client) this);
    }

}
