package com.algorand.algosdk.v2.client.indexer;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.AssetBalancesResponse;


/**
 * Lookup the list of accounts who hold this asset
 * /v2/assets/{asset-id}/balances
 */
public class LookupAssetBalances extends Query {

    private Long assetId;

    /**
     * @param assetId
     */
    public LookupAssetBalances(Client client, Long assetId) {
        super(client, new HttpMethod("get"));
        this.assetId = assetId;
    }

    /**
     * Results should have an amount greater than this value. MicroAlgos are the
     * default currency unless an asset-id is provided, in which case the asset will be
     * used.
     */
    public LookupAssetBalances currencyGreaterThan(Long currencyGreaterThan) {
        addQuery("currency-greater-than", String.valueOf(currencyGreaterThan));
        return this;
    }

    /**
     * Results should have an amount less than this value. MicroAlgos are the default
     * currency unless an asset-id is provided, in which case the asset will be used.
     */
    public LookupAssetBalances currencyLessThan(Long currencyLessThan) {
        addQuery("currency-less-than", String.valueOf(currencyLessThan));
        return this;
    }

    /**
     * Include all items including closed accounts, deleted applications, destroyed
     * assets, opted-out asset holdings, and closed-out application localstates.
     */
    public LookupAssetBalances includeAll(Boolean includeAll) {
        addQuery("include-all", String.valueOf(includeAll));
        return this;
    }

    /**
     * Maximum number of results to return. There could be additional pages even if the
     * limit is not reached.
     */
    public LookupAssetBalances limit(Long limit) {
        addQuery("limit", String.valueOf(limit));
        return this;
    }

    /**
     * The next page of results. Use the next token provided by the previous results.
     */
    public LookupAssetBalances next(String next) {
        addQuery("next", String.valueOf(next));
        return this;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<AssetBalancesResponse> execute() throws Exception {
        Response<AssetBalancesResponse> resp = baseExecute();
        resp.setValueType(AssetBalancesResponse.class);
        return resp;
    }

   /**
    * Execute the query with custom headers, there must be an equal number of keys and values
    * or else an error will be generated.
    * @param headers an array of header keys
    * @param values an array of header values
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<AssetBalancesResponse> execute(String[] headers, String[] values) throws Exception {
        Response<AssetBalancesResponse> resp = baseExecute(headers, values);
        resp.setValueType(AssetBalancesResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (this.assetId == null) {
            throw new RuntimeException("asset-id is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("assets"));
        addPathSegment(String.valueOf(assetId));
        addPathSegment(String.valueOf("balances"));

        return qd;
    }
}
