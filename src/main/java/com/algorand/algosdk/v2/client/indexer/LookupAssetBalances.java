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
	public Long assetId() {
		return this.assetId;
	}
	private Long currencyGreaterThan;
	public Long currencyGreaterThan() {
		return this.currencyGreaterThan;
	}
	private Long currencyLessThan;
	public Long currencyLessThan() {
		return this.currencyLessThan;
	}
	private Long limit;
	public Long limit() {
		return this.limit;
	}
	private String next;
	public String next() {
		return this.next;
	}
	private Long round;
	public Long round() {
		return this.round;
	}

	public LookupAssetBalances(Client client, Long assetId) {
		super(client, "get");
		this.assetId = assetId;
	}

	/**
	 * Results should have an amount greater than this value. MicroAlgos are the 
	 * default currency unless an asset-id is provided, in which case the asset will be 
	 * used. 
	 */
	public LookupAssetBalances currencyGreaterThan(Long currencyGreaterThan) {
		this.currencyGreaterThan = currencyGreaterThan;
		addQuery("currency-greater-than", String.valueOf(currencyGreaterThan));
		return this;
	}

	/**
	 * Results should have an amount less than this value. MicroAlgos are the default 
	 * currency unless an asset-id is provided, in which case the asset will be used. 
	 */
	public LookupAssetBalances currencyLessThan(Long currencyLessThan) {
		this.currencyLessThan = currencyLessThan;
		addQuery("currency-less-than", String.valueOf(currencyLessThan));
		return this;
	}

	/**
	 * Maximum number of results to return. 
	 */
	public LookupAssetBalances limit(Long limit) {
		this.limit = limit;
		addQuery("limit", String.valueOf(limit));
		return this;
	}

	/**
	 * The next page of results. Use the next token provided by the previous results. 
	 */
	public LookupAssetBalances next(String next) {
		this.next = next;
		addQuery("next", String.valueOf(next));
		return this;
	}

	/**
	 * Include results for the specified round. 
	 */
	public LookupAssetBalances round(Long round) {
		this.round = round;
		addQuery("round", String.valueOf(round));
		return this;
	}

	@Override
	public Response<AssetBalancesResponse> execute() throws Exception {
		Response<AssetBalancesResponse> resp = baseExecute();
		resp.setValueType(AssetBalancesResponse.class);
		return resp;
	}
	protected QueryData getRequestString() {
		addPathSegment(String.valueOf("assets"));
		addPathSegment(String.valueOf(assetId));
		addPathSegment(String.valueOf("balances"));

		return qd;
	}
}