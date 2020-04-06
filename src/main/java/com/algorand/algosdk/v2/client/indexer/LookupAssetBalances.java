package com.algorand.algosdk.v2.client.indexer;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.algorand.algosdk.v2.client.model.AssetBalancesResponse;


/*
	Lookup the list of accounts who hold this asset 
 */
public class LookupAssetBalances extends Query {
	private long assetId;
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

	public LookupAssetBalances(Client client) {
		super(client);
	}
	public LookupAssetBalances setAssetId(long assetId) {
		this.assetId = assetId;
		this.assetIdIsSet = true;
		return this;
	}
	public LookupAssetBalances setCurrencyGreaterThan(long currencyGreaterThan) {
		this.currencyGreaterThan = currencyGreaterThan;
		this.currencyGreaterThanIsSet = true;
		return this;
	}
	public LookupAssetBalances setCurrencyLessThan(long currencyLessThan) {
		this.currencyLessThan = currencyLessThan;
		this.currencyLessThanIsSet = true;
		return this;
	}
	public LookupAssetBalances setLimit(long limit) {
		this.limit = limit;
		this.limitIsSet = true;
		return this;
	}
	public LookupAssetBalances setNext(String next) {
		this.next = next;
		this.nextIsSet = true;
		return this;
	}
	public LookupAssetBalances setRound(long round) {
		this.round = round;
		this.roundIsSet = true;
		return this;
	}

	public AssetBalancesResponse lookup() {
		String response;
		try {
			response = request();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		AssetBalancesResponse resp;
		try {
			resp = mapper.readValue(response, AssetBalancesResponse.class);
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
		sb.append("assets");
		sb.append("/");
		sb.append(assetId);
		sb.append("/");
		sb.append("balances");
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