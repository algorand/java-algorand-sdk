package com.algorand.algosdk.v2.client.indexer;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.*;
import com.algorand.algosdk.v2.client.model.AssetsResponse;


/**
 * Lookup an account's created asset parameters, optionally for a specific ID.
 * /v2/accounts/{account-id}/created-assets
 */
public class LookupAccountCreatedAssets extends Query {

    private Address accountId;

    /**
     * @param accountId account string
     */
    public LookupAccountCreatedAssets(Client client, Address accountId) {
        super(client, new HttpMethod("get"));
        this.accountId = accountId;
    }

    /**
     * Asset ID
     */
    public LookupAccountCreatedAssets assetId(Long assetId) {
        addQuery("asset-id", String.valueOf(assetId));
        return this;
    }

    /**
     * Include all items including closed accounts, deleted applications, destroyed
     * assets, opted-out asset holdings, and closed-out application localstates.
     */
    public LookupAccountCreatedAssets includeAll(Boolean includeAll) {
        addQuery("include-all", String.valueOf(includeAll));
        return this;
    }

    /**
     * Maximum number of results to return. There could be additional pages even if the
     * limit is not reached.
     */
    public LookupAccountCreatedAssets limit(Long limit) {
        addQuery("limit", String.valueOf(limit));
        return this;
    }

    /**
     * The next page of results. Use the next token provided by the previous results.
     */
    public LookupAccountCreatedAssets next(String next) {
        addQuery("next", String.valueOf(next));
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
        if (this.accountId == null) {
            throw new RuntimeException("account-id is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("accounts"));
        addPathSegment(String.valueOf(accountId));
        addPathSegment(String.valueOf("created-assets"));

        return qd;
    }
}
