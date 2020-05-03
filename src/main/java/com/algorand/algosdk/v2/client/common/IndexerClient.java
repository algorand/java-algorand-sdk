package com.algorand.algosdk.v2.client.common;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.indexer.SearchForAccounts;
import com.algorand.algosdk.v2.client.indexer.LookupAccountByID;
import com.algorand.algosdk.v2.client.indexer.LookupAccountTransactions;
import com.algorand.algosdk.v2.client.indexer.SearchForAssets;
import com.algorand.algosdk.v2.client.indexer.LookupAssetByID;
import com.algorand.algosdk.v2.client.indexer.LookupAssetBalances;
import com.algorand.algosdk.v2.client.indexer.LookupAssetTransactions;
import com.algorand.algosdk.v2.client.indexer.LookupBlock;
import com.algorand.algosdk.v2.client.indexer.SearchForTransactions;

public class IndexerClient extends Client {

	public IndexerClient(String host, int port, String token) {
		super(host, port, token, "X-Indexer-API-Token");
	}
	/**
	 * Search for accounts. 
	 * /accounts 
	 */
	public SearchForAccounts searchForAccounts() {
		return new SearchForAccounts((Client) this);
	}

	/**
	 * Lookup account information. 
	 * /accounts/{account-id} 
	 */
	public LookupAccountByID lookupAccountByID(Address accountId) {
		return new LookupAccountByID((Client) this, accountId);
	}

	/**
	 * Lookup account transactions. 
	 * /accounts/{account-id}/transactions 
	 */
	public LookupAccountTransactions lookupAccountTransactions(Address accountId) {
		return new LookupAccountTransactions((Client) this, accountId);
	}

	/**
	 * Search for assets. 
	 * /assets 
	 */
	public SearchForAssets searchForAssets() {
		return new SearchForAssets((Client) this);
	}

	/**
	 * Lookup asset information. 
	 * /assets/{asset-id} 
	 */
	public LookupAssetByID lookupAssetByID(Long assetId) {
		return new LookupAssetByID((Client) this, assetId);
	}

	/**
	 * Lookup the list of accounts who hold this asset 
	 * /assets/{asset-id}/balances 
	 */
	public LookupAssetBalances lookupAssetBalances(Long assetId) {
		return new LookupAssetBalances((Client) this, assetId);
	}

	/**
	 * Lookup transactions for an asset. 
	 * /assets/{asset-id}/transactions 
	 */
	public LookupAssetTransactions lookupAssetTransactions(Long assetId) {
		return new LookupAssetTransactions((Client) this, assetId);
	}

	/**
	 * Lookup block. 
	 * /blocks/{round-number} 
	 */
	public LookupBlock lookupBlock(Long roundNumber) {
		return new LookupBlock((Client) this, roundNumber);
	}

	/**
	 * Search for transactions. 
	 * /transactions 
	 */
	public SearchForTransactions searchForTransactions() {
		return new SearchForTransactions((Client) this);
	}

}
