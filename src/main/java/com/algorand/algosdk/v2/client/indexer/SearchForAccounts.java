package com.algorand.algosdk.v2.client.indexer;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.AccountsResponse;


/**
 * Search for accounts. 
 * /v2/accounts 
 */
public class SearchForAccounts extends Query {

	public SearchForAccounts(Client client) {
		super(client, new HttpMethod("get"));
	}

	/**
	 * Asset ID 
	 */
	public SearchForAccounts assetId(Long assetId) {
		addQuery("asset-id", String.valueOf(assetId));
		return this;
	}

	/**
	 * Results should have an amount greater than this value. MicroAlgos are the 
	 * default currency unless an asset-id is provided, in which case the asset will be 
	 * used. 
	 */
	public SearchForAccounts currencyGreaterThan(Long currencyGreaterThan) {
		addQuery("currency-greater-than", String.valueOf(currencyGreaterThan));
		return this;
	}

	/**
	 * Results should have an amount less than this value. MicroAlgos are the default 
	 * currency unless an asset-id is provided, in which case the asset will be used. 
	 */
	public SearchForAccounts currencyLessThan(Long currencyLessThan) {
		addQuery("currency-less-than", String.valueOf(currencyLessThan));
		return this;
	}

	/**
	 * Maximum number of results to return. 
	 */
	public SearchForAccounts limit(Long limit) {
		addQuery("limit", String.valueOf(limit));
		return this;
	}

	/**
	 * The next page of results. Use the next token provided by the previous results. 
	 */
	public SearchForAccounts next(String next) {
		addQuery("next", String.valueOf(next));
		return this;
	}

	/**
	 * Include results for the specified round. For performance reasons, this parameter 
	 * may be disabled on some configurations. 
	 */
	public SearchForAccounts round(Long round) {
		addQuery("round", String.valueOf(round));
		return this;
	}

	@Override
	public Response<AccountsResponse> execute() throws Exception {
		Response<AccountsResponse> resp = baseExecute();
		resp.setValueType(AccountsResponse.class);
		return resp;
	}

	protected QueryData getRequestString() {
		addPathSegment(String.valueOf("v2"));
		addPathSegment(String.valueOf("accounts"));

		return qd;
	}
}