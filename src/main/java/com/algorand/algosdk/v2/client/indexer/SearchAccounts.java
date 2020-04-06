package com.algorand.algosdk.v2.client.indexer;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.algorand.algosdk.v2.client.model.AccountsResponse;


/*
	Search for accounts. 
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
			response = request();
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
	protected String getRequestString() {
		StringBuffer sb = new StringBuffer();
		sb.append("/");
		sb.append("accounts");
		sb.append("?");

		boolean added = false;

		if (this.assetIdIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("assetId=");
			sb.append(assetId);
			added = true;
		}
		if (this.currencyGreaterThanIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("currencyGreaterThan=");
			sb.append(currencyGreaterThan);
			added = true;
		}
		if (this.currencyLessThanIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("currencyLessThan=");
			sb.append(currencyLessThan);
			added = true;
		}
		if (this.limitIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("limit=");
			sb.append(limit);
			added = true;
		}
		if (this.nextIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("next=");
			sb.append(next);
			added = true;
		}
		if (this.roundIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("round=");
			sb.append(round);
			added = true;
		}

		return sb.toString();
	}
}