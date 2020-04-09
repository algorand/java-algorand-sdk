package com.algorand.algosdk.v2.client.indexer;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.algorand.algosdk.v2.client.connect.QueryData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.algorand.algosdk.v2.client.model.AccountsResponse;


/*
	Search for accounts. /accounts 
 */
public class SearchAccounts extends Query {
	private String assetId;
	private long currencyGreaterThan;
	private long currencyLessThan;
	private long limit;
	private String next;
	private long round;

	private boolean assetIdIsSet;
	private boolean currencyGreaterThanIsSet;
	private boolean currencyLessThanIsSet;
	private boolean limitIsSet;
	private boolean nextIsSet;
	private boolean roundIsSet;

	public SearchAccounts(Client client) {
		super(client);
	}
	public SearchAccounts setAssetId(String assetId) {
		this.assetId = assetId;
		this.assetIdIsSet = true;
		return this;
	}
	public SearchAccounts setCurrencyGreaterThan(long currencyGreaterThan) {
		this.currencyGreaterThan = currencyGreaterThan;
		this.currencyGreaterThanIsSet = true;
		return this;
	}
	public SearchAccounts setCurrencyLessThan(long currencyLessThan) {
		this.currencyLessThan = currencyLessThan;
		this.currencyLessThanIsSet = true;
		return this;
	}
	public SearchAccounts setLimit(long limit) {
		this.limit = limit;
		this.limitIsSet = true;
		return this;
	}
	public SearchAccounts setNext(String next) {
		this.next = next;
		this.nextIsSet = true;
		return this;
	}
	public SearchAccounts setRound(long round) {
		this.round = round;
		this.roundIsSet = true;
		return this;
	}

	public AccountsResponse lookup() {
		String response;
		try {
			response = request("get");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		AccountsResponse resp;
		try {
			resp = mapper.readValue(response, AccountsResponse.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return resp;
	}
	public QueryData getRequestString() {
		QueryData qd = new QueryData();
		if (this.assetIdIsSet) {
			qd.addQuery("assetId", String.valueOf(assetId));
		}
		if (this.currencyGreaterThanIsSet) {
			qd.addQuery("currencyGreaterThan", String.valueOf(currencyGreaterThan));
		}
		if (this.currencyLessThanIsSet) {
			qd.addQuery("currencyLessThan", String.valueOf(currencyLessThan));
		}
		if (this.limitIsSet) {
			qd.addQuery("limit", String.valueOf(limit));
		}
		if (this.nextIsSet) {
			qd.addQuery("next", String.valueOf(next));
		}
		if (this.roundIsSet) {
			qd.addQuery("round", String.valueOf(round));
		}
		qd.addPathSegment(String.valueOf("accounts"));

		return qd;
	}
}