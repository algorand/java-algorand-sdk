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
	private Long assetId;
	private Long currencyGreaterThan;
	private Long currencyLessThan;
	private Long limit;
	private String next;
	private Long round;


	public SearchAccounts(Client client) {
		super(client, "get");
	}

	/**
	 * Asset ID 
	 */
	public SearchAccounts setAssetId(Long assetId) {
		this.assetId = assetId;
		return this;
	}

	/**
	 * Results should have an amount greater than this value. MicroAlgos are the 
	 * default currency unless an asset-id is provided, in which case the asset will be 
	 * used. 
	 */
	public SearchAccounts setCurrencyGreaterThan(Long currencyGreaterThan) {
		this.currencyGreaterThan = currencyGreaterThan;
		return this;
	}

	/**
	 * Results should have an amount less than this value. MicroAlgos are the default 
	 * currency unless an asset-id is provided, in which case the asset will be used. 
	 */
	public SearchAccounts setCurrencyLessThan(Long currencyLessThan) {
		this.currencyLessThan = currencyLessThan;
		return this;
	}

	/**
	 * Maximum number of results to return. 
	 */
	public SearchAccounts setLimit(Long limit) {
		this.limit = limit;
		return this;
	}

	/**
	 * The next page of results. Use the next token provided by the previous results. 
	 */
	public SearchAccounts setNext(String next) {
		this.next = next;
		return this;
	}

	/**
	 * Include results for the specified round. For performance reasons, this parameter 
	 * may be disabled on some configurations. 
	 */
	public SearchAccounts setRound(Long round) {
		this.round = round;
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
		if (this.assetId != null) {
			qd.addQuery("assetId", String.valueOf(assetId));
		}
		if (this.currencyGreaterThan != null) {
			qd.addQuery("currencyGreaterThan", String.valueOf(currencyGreaterThan));
		}
		if (this.currencyLessThan != null) {
			qd.addQuery("currencyLessThan", String.valueOf(currencyLessThan));
		}
		if (this.limit != null) {
			qd.addQuery("limit", String.valueOf(limit));
		}
		if (this.next != null) {
			qd.addQuery("next", String.valueOf(next));
		}
		if (this.round != null) {
			qd.addQuery("round", String.valueOf(round));
		}
		qd.addPathSegment(String.valueOf("accounts"));

		return qd;
	}
}