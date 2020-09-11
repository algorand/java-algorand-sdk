package com.algorand.algosdk.v2.client.indexer;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.AssetsResponse;


/**
 * Search for assets.
 * /v2/assets
 */
public class SearchForAssets extends Query {

    public SearchForAssets(Client client) {
        super(client, new HttpMethod("get"));
    }

    /**
     * Asset ID
     */
    public SearchForAssets assetId(Long assetId) {
        addQuery("asset-id", String.valueOf(assetId));
        return this;
    }

    /**
     * Filter just assets with the given creator address.
     */
    public SearchForAssets creator(String creator) {
        addQuery("creator", String.valueOf(creator));
        return this;
    }

    /**
     * Maximum number of results to return.
     */
    public SearchForAssets limit(Long limit) {
        addQuery("limit", String.valueOf(limit));
        return this;
    }

    /**
     * Filter just assets with the given name.
     */
    public SearchForAssets name(String name) {
        addQuery("name", String.valueOf(name));
        return this;
    }

    /**
     * The next page of results. Use the next token provided by the previous results.
     */
    public SearchForAssets next(String next) {
        addQuery("next", String.valueOf(next));
        return this;
    }

    /**
     * Filter just assets with the given unit.
     */
    public SearchForAssets unit(String unit) {
        addQuery("unit", String.valueOf(unit));
        return this;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<AssetsResponse> execute() throws Exception {
        Response<AssetsResponse> resp = baseExecute();
        resp.setValueType(AssetsResponse.class);
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
    public Response<AssetsResponse> execute(String[] headers, String[] values) throws Exception {
        Response<AssetsResponse> resp = baseExecute(headers, values);
        resp.setValueType(AssetsResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("assets"));

        return qd;
    }
}
