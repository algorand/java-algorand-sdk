package com.algorand.indexer.lookup;

import com.algorand.indexer.client.Query;
import com.algorand.indexer.client.Client;
import com.algorand.indexer.schemas.AssetBalances;

public class LookupAssetBalances extends Query {
	private long limit;
	private String next;
	private long round;
	private long currencyGreaterThan;
	private long currencyLessThan;
	private long assetId;
	
	private boolean limitSet;
	private boolean nextSet;
	private boolean roundSet;
	private boolean currencyGreaterThanSet;
	private boolean currencyLessThanSet;
	private boolean assetIdSet;

	public LookupAssetBalances(Client client) {
		super(client);
	}

	public LookupAssetBalances setLimit(long limit) {
		this.limit = limit;
		this.limitSet = true;
		return this;
	}

	public LookupAssetBalances setNext(String next) {
		this.next = next;
		this.nextSet = true;
		return this;
	}

	public LookupAssetBalances setRound(long round) {
		this.round = round;
		this.roundSet = true;
		return this;
	}

	public LookupAssetBalances setCurrencyGreaterThan(long currencyGreaterThan) {
		this.currencyGreaterThan = currencyGreaterThan;
		this.currencyGreaterThanSet = true;
		return this;
	}

	public LookupAssetBalances setCurrencyLessThan(long currencyLessThan) {
		this.currencyLessThan = currencyLessThan;
		this.currencyLessThanSet = true;
		return this;
	}

	public LookupAssetBalances setAssetId(long assetId) {
		this.assetId = assetId;
		this.assetIdSet = true;
		return this;
	}
	
	public AssetBalances lookup() {
		String response;
		try {
			response = request();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return new AssetBalances(response);
	}

	protected String getRequestString() {
		if (!this.assetIdSet) {
			throw new RuntimeException("AssetId is not set!");
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("assets/");
		sb.append(assetId);
		sb.append("balances?");

		boolean added = false;
		
		if (this.limitSet) {
			sb.append("limit=");
			sb.append(this.limit);
			added = true;
		}
			
		if (this.nextSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("next=");
			sb.append(next);
		}
		
		
		if (this.roundSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("round=");
			sb.append(round);
		}

		if (this.currencyGreaterThanSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("currencyGreaterThan=");
			sb.append(currencyGreaterThan);
		}

		if (this.currencyLessThanSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("currencyLessThan=");
			sb.append(currencyLessThan);
		}
		
		return sb.toString();
	}
}
