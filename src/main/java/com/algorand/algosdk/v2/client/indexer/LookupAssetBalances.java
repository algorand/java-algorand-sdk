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

    public LookupAssetBalances assetId(Long assetId) {
        addQuery("asset-id", String.valueOf(assetId));
        return this;
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
     * Maximum number of results to return.
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
     * Include results for the specified round.
     */
    public LookupAssetBalances round(Long round) {
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
        if (!qd.queries.containsKey("assetId")) {
            throw new RuntimeException("asset-id is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("assets"));
        addPathSegment(String.valueOf(assetId));
        addPathSegment(String.valueOf("balances"));

        return qd;
    }
}