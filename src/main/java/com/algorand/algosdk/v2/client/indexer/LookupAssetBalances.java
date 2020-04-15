package com.algorand.algosdk.v2.client.indexer;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.AssetBalancesResponse;


/**
 * Lookup the list of accounts who hold this asset /assets/{asset-id}/balances 
 */
public class LookupAssetBalances extends Query {
	private Long assetId;
	private Long currencyGreaterThan;
	private Long currencyLessThan;
	private Long limit;
	private String next;
	private Long round;


	public LookupAssetBalances(Client client, Long assetId) {
		super(client, "get");
		this.assetId = assetId;
	}

	/**
	 * Results should have an amount greater than this value. MicroAlgos are the 
	 * default currency unless an asset-id is provided, in which case the asset will be 
	 * used. 
	 */
	public LookupAssetBalances setCurrencyGreaterThan(Long currencyGreaterThan) {
		this.currencyGreaterThan = currencyGreaterThan;
		return this;
	}

	/**
	 * Results should have an amount less than this value. MicroAlgos are the default 
	 * currency unless an asset-id is provided, in which case the asset will be used. 
	 */
	public LookupAssetBalances setCurrencyLessThan(Long currencyLessThan) {
		this.currencyLessThan = currencyLessThan;
		return this;
	}

	/**
	 * Maximum number of results to return. 
	 */
	public LookupAssetBalances setLimit(Long limit) {
		this.limit = limit;
		return this;
	}

	/**
	 * The next page of results. Use the next token provided by the previous results. 
	 */
	public LookupAssetBalances setNext(String next) {
		this.next = next;
		return this;
	}

	/**
	 * Include results for the specified round. 
	 */
	public LookupAssetBalances setRound(Long round) {
		this.round = round;
		return this;
	}

	@Override
	public Response<AssetBalancesResponse> execute() throws Exception {
		Response<AssetBalancesResponse> resp = baseExecute();
		resp.setValueType(AssetBalancesResponse.class);
		return resp;
	}
	public QueryData getRequestString() {
		QueryData qd = new QueryData();
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
		qd.addPathSegment(String.valueOf("assets"));
		qd.addPathSegment(String.valueOf(assetId));
		qd.addPathSegment(String.valueOf("balances"));

		return qd;
	}
}