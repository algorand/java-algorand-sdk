package com.algorand.algosdk.v2.client.common;

import com.algorand.algosdk.v2.client.indexer.MakeHealthCheck;
import com.algorand.algosdk.v2.client.indexer.SearchForAccounts;
import com.algorand.algosdk.v2.client.indexer.LookupAccountByID;
import com.algorand.algosdk.v2.client.indexer.LookupAccountTransactions;
import com.algorand.algosdk.v2.client.indexer.SearchForAssets;
import com.algorand.algosdk.v2.client.indexer.LookupAssetByID;
import com.algorand.algosdk.v2.client.indexer.LookupAssetBalances;
import com.algorand.algosdk.v2.client.indexer.LookupAssetTransactions;
import com.algorand.algosdk.v2.client.indexer.LookupBlock;
import com.algorand.algosdk.v2.client.indexer.SearchForTransactions;
import com.algorand.algosdk.crypto.Address;

public class IndexerClient extends Client {

	/**
	 * Construct a IndexerClient for communicating with the REST API.
	 * @param host using a URI format. If the scheme is not supplied the client will use HTTP.
	 * @param port REST server port.
	 * @param token authentication token.
	 */
	public IndexerClient(String host, int port, String token) {
		super(host, port, token, "X-Indexer-API-Token");
	}

	/**
	 * Construct a IndexerClient for communicating with the REST API.
	 * @param host using a URI format. If the scheme is not supplied the client will use HTTP.
	 * @param port REST server port.
	 */
	public IndexerClient(String host, int port) {
		super(host, port, "", "X-Indexer-API-Token");
	}

	/**
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
	 * Lookup account transactions. 
	 * /v2/accounts/{account-id}/transactions 
	 */
	public LookupAccountTransactions lookupAccountTransactions(Address accountId) {
		return new LookupAccountTransactions((Client) this, accountId);
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
	 * Lookup transactions for an asset. 
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
	 * Search for transactions. 
	 * /v2/transactions 
	 */
	public SearchForTransactions searchForTransactions() {
		return new SearchForTransactions((Client) this);
	}

}
