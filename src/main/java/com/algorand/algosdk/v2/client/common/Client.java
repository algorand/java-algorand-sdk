package com.algorand.algosdk.v2.client.common;

import java.util.Map.Entry;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.algorand.algosdk.crypto.Address;

import com.algorand.algosdk.v2.client.algod.AccountInformation;
import com.algorand.algosdk.v2.client.algod.GetPendingTransactionsByAddress;
import com.algorand.algosdk.v2.client.algod.GetBlock;
import com.algorand.algosdk.v2.client.algod.GetSupply;
import com.algorand.algosdk.v2.client.algod.RegisterParticipationKeys;
import com.algorand.algosdk.v2.client.algod.ShutdownNode;
import com.algorand.algosdk.v2.client.algod.GetStatus;
import com.algorand.algosdk.v2.client.algod.WaitForBlock;
import com.algorand.algosdk.v2.client.algod.RawTransaction;
import com.algorand.algosdk.v2.client.algod.TransactionParams;
import com.algorand.algosdk.v2.client.algod.GetPendingTransactions;
import com.algorand.algosdk.v2.client.algod.PendingTransactionInformation;
import com.algorand.algosdk.v2.client.indexer.SearchForAccounts;
import com.algorand.algosdk.v2.client.indexer.LookupAccountByID;
import com.algorand.algosdk.v2.client.indexer.LookupAccountTransactions;
import com.algorand.algosdk.v2.client.indexer.SearchForAssets;
import com.algorand.algosdk.v2.client.indexer.LookupAssetByID;
import com.algorand.algosdk.v2.client.indexer.LookupAssetBalances;
import com.algorand.algosdk.v2.client.indexer.LookupAssetTransactions;
import com.algorand.algosdk.v2.client.indexer.LookupBlock;
import com.algorand.algosdk.v2.client.indexer.SearchForTransactions;

public class Client {

	private OkHttpClient client;
	private String host;
	private int port;
	private String token;

	public int getPort() {
		return port;
	}

	public String getHost() {
		return host;
	}

	public static HttpUrl getHttpUrl(QueryData qData, int port, String host) {
		HttpUrl.Builder httpUrlBuilder = (new HttpUrl.Builder()).scheme("http").port(port).host(host);
		for (String ps : qData.pathSegments) {
			httpUrlBuilder.addPathSegment(ps);
		}
		for (Entry<String, String> kvp : qData.queries.entrySet()) {
			httpUrlBuilder.addQueryParameter(kvp.getKey(), kvp.getValue());
		}
		HttpUrl httpUrl = httpUrlBuilder.build();
		return httpUrl;
	}

	public Client(String host, int port, String token) {
		MediaType.parse("application/json; charset=utf-8");
		this.host = host;
		this.port = port;
		this.token = token;
		this.client = new OkHttpClient();
	}

	public Response executeCall(QueryData qData, String getOrPost) throws Exception {

		HttpUrl httpUrl = getHttpUrl(qData, port, host);
		Builder reqBuilder = new Request.Builder().url(httpUrl);
		if (token != null) {
			reqBuilder.addHeader("X-Algo-API-Token", token);
		}
		RequestBody rb = RequestBody.create(
				MediaType.parse("Binary data"),
				qData.bodySegments.isEmpty() ? new byte[0] : qData.bodySegments.get(0));

		if (getOrPost.equals("get")) {
			reqBuilder.get();
		} else {
			reqBuilder.post(rb);
		}

		Request request = reqBuilder.build();
		Response response = null;
		try {
			response = client.newCall(request).execute();
		} catch (Exception e) {
			throw e;
		}
		return response;
	}
	
	/**
	 * Given a specific account public key, this call returns the accounts status, 
	 * balance and spendable amounts 
	 * /v2/accounts/{address} 
	 */
	public AccountInformation AccountInformation(Address address) {
		return new AccountInformation((Client) this, address);
	}

	/**
	 * Get the list of pending transactions by address, sorted by priority, in 
	 * decreasing order, truncated at the end at MAX. If MAX = 0, returns all pending 
	 * transactions. 
	 * /v2/accounts/{address}/transactions/pending 
	 */
	public GetPendingTransactionsByAddress GetPendingTransactionsByAddress(Address address) {
		return new GetPendingTransactionsByAddress((Client) this, address);
	}

	/**
	 * /v2/blocks/{round} 
	 */
	public GetBlock GetBlock(Long round) {
		return new GetBlock((Client) this, round);
	}

	/**
	 * /v2/ledger/supply 
	 */
	public GetSupply GetSupply() {
		return new GetSupply((Client) this);
	}

	/**
	 * Generate (or renew) and register participation keys on the node for a given 
	 * account address. 
	 * /v2/register-participation-keys/{address} 
	 */
	public RegisterParticipationKeys RegisterParticipationKeys(Address address) {
		return new RegisterParticipationKeys((Client) this, address);
	}

	/**
	 * Special management endpoint to shutdown the node. Optionally provide a timeout 
	 * parameter to indicate that the node should begin shutting down after a number of 
	 * seconds. 
	 * /v2/shutdown 
	 */
	public ShutdownNode ShutdownNode() {
		return new ShutdownNode((Client) this);
	}

	/**
	 * /v2/status 
	 */
	public GetStatus GetStatus() {
		return new GetStatus((Client) this);
	}

	/**
	 * Waits for a block to appear after round {round} and returns the node's status at 
	 * the time. 
	 * /v2/status/wait-for-block-after/{round}/ 
	 */
	public WaitForBlock WaitForBlock(Long round) {
		return new WaitForBlock((Client) this, round);
	}

	/**
	 * /v2/transactions 
	 */
	public RawTransaction RawTransaction() {
		return new RawTransaction((Client) this);
	}

	/**
	 * /v2/transactions/params 
	 */
	public TransactionParams TransactionParams() {
		return new TransactionParams((Client) this);
	}

	/**
	 * Get the list of pending transactions, sorted by priority, in decreasing order, 
	 * truncated at the end at MAX. If MAX = 0, returns all pending transactions. 
	 * /v2/transactions/pending 
	 */
	public GetPendingTransactions GetPendingTransactions() {
		return new GetPendingTransactions((Client) this);
	}

	/**
	 * Given a transaction id of a recently submitted transaction, it returns 
	 * information about it. There are several cases when this might succeed: 
	 * - transaction committed (committed round > 0) - transaction still in the pool 
	 * (committed round = 0, pool error = "") - transaction removed from pool due to 
	 * error (committed round = 0, pool error != "") 
	 * Or the transaction may have happened sufficiently long ago that the node no 
	 * longer remembers it, and this will return an error. 
	 * /v2/transactions/pending/{txid} 
	 */
	public PendingTransactionInformation PendingTransactionInformation(String txid) {
		return new PendingTransactionInformation((Client) this, txid);
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
