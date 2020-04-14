package com.algorand.algosdk.v2.client.indexer;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.AccountsResponse;


/**
 * Search for accounts. /accounts 
 */
public class SearchAccounts extends Query {
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

	public SearchAccounts(Client client) {
		super(client, "get");
	}

	/**
	 * Asset ID 
	 */
	public SearchAccounts setAssetId(long assetId) {
		this.assetId = assetId;
		this.assetIdIsSet = true;
		return this;
	}

	/**
	 * Results should have an amount greater than this value. MicroAlgos are the 
	 * default currency unless an asset-id is provided, in which case the asset will be 
	 * used. 
	 */
	public SearchAccounts setCurrencyGreaterThan(long currencyGreaterThan) {
		this.currencyGreaterThan = currencyGreaterThan;
		this.currencyGreaterThanIsSet = true;
		return this;
	}

	/**
	 * Results should have an amount less than this value. MicroAlgos are the default 
	 * currency unless an asset-id is provided, in which case the asset will be used. 
	 */
	public SearchAccounts setCurrencyLessThan(long currencyLessThan) {
		this.currencyLessThan = currencyLessThan;
		this.currencyLessThanIsSet = true;
		return this;
	}

	/**
	 * Maximum number of results to return. 
	 */
	public SearchAccounts setLimit(long limit) {
		this.limit = limit;
		this.limitIsSet = true;
		return this;
	}

	/**
	 * The next page of results. Use the next token provided by the previous results. 
	 */
	public SearchAccounts setNext(String next) {
		this.next = next;
		this.nextIsSet = true;
		return this;
	}

	/**
	 * Include results for the specified round. For performance reasons, this parameter 
	 * may be disabled on some configurations. 
	 */
	public SearchAccounts setRound(long round) {
		this.round = round;
		this.roundIsSet = true;
		return this;
	}

	@Override
	public Response<AccountsResponse> execute() throws Exception {
		Response<AccountsResponse> resp = baseExecute();
		resp.setValueType(AccountsResponse.class);
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