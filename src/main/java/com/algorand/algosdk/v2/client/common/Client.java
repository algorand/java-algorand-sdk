package com.algorand.algosdk.v2.client.common;

import java.util.Map.Entry;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

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
import com.algorand.algosdk.v2.client.indexer.SearchAccounts;
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
	
	public Client(String host, int port) {
		MediaType.parse("application/json; charset=utf-8");
		this.host = host;
		this.port = port;
		this.client = new OkHttpClient();
	}
	
	public Response executeCall(QueryData qData, String getOrPost) throws Exception {
		
		HttpUrl httpUrl = getHttpUrl(qData, port, host);
		Request request = new Request.Builder()
				.url(httpUrl)
				.get()
				.build();
/*		RequestBody body = RequestBody.create(mediaType, requestString);
		Request request = new Request.Builder()
				.url(httpUrl)
				.post(body)
				.build();*/
		Response response = null;
		try {
			response = client.newCall(request).execute();	        
		} catch (Exception e) {
			throw e;
		}
		return response;
		/*
		String responseString = response.body().string();
		if (response.code() != 200) {
			throw new RuntimeException(responseString);
		}
		return responseString;
        // String contentType = response.headers().get("Content-Type");
*/
	}
	
	public AccountInformation AccountInformation(String address) {
		return new AccountInformation((Client) this, address);
	}
	public GetPendingTransactionsByAddress GetPendingTransactionsByAddress(String address) {
		return new GetPendingTransactionsByAddress((Client) this, address);
	}
	public GetBlock GetBlock(long round) {
		return new GetBlock((Client) this, round);
	}
	public GetSupply GetSupply() {
		return new GetSupply((Client) this);
	}
	public RegisterParticipationKeys RegisterParticipationKeys(String address) {
		return new RegisterParticipationKeys((Client) this, address);
	}
	public ShutdownNode ShutdownNode() {
		return new ShutdownNode((Client) this);
	}
	public GetStatus GetStatus() {
		return new GetStatus((Client) this);
	}
	public WaitForBlock WaitForBlock(long round) {
		return new WaitForBlock((Client) this, round);
	}
	public RawTransaction RawTransaction() {
		return new RawTransaction((Client) this);
	}
	public TransactionParams TransactionParams() {
		return new TransactionParams((Client) this);
	}
	public GetPendingTransactions GetPendingTransactions() {
		return new GetPendingTransactions((Client) this);
	}
	public PendingTransactionInformation PendingTransactionInformation(String txid) {
		return new PendingTransactionInformation((Client) this, txid);
	}
	public SearchAccounts searchAccounts() {
		return new SearchAccounts((Client) this);
	}
	public LookupAccountByID lookupAccountByID(String accountId) {
		return new LookupAccountByID((Client) this, accountId);
	}
	public LookupAccountTransactions lookupAccountTransactions(String accountId) {
		return new LookupAccountTransactions((Client) this, accountId);
	}
	public SearchForAssets searchForAssets() {
		return new SearchForAssets((Client) this);
	}
	public LookupAssetByID lookupAssetByID(long assetId) {
		return new LookupAssetByID((Client) this, assetId);
	}
	public LookupAssetBalances lookupAssetBalances(long assetId) {
		return new LookupAssetBalances((Client) this, assetId);
	}
	public LookupAssetTransactions lookupAssetTransactions(long assetId) {
		return new LookupAssetTransactions((Client) this, assetId);
	}
	public LookupBlock lookupBlock(long roundNumber) {
		return new LookupBlock((Client) this, roundNumber);
	}
	public SearchForTransactions searchForTransactions() {
		return new SearchForTransactions((Client) this);
	}

}
