package com.algorand.algosdk.v2.client.indexer;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.AccountsResponse;
import com.algorand.algosdk.v2.client.model.Enums;


/**
 * Search for accounts.
 * /v2/accounts
 */
public class SearchForAccounts extends Query {

    public SearchForAccounts(Client client) {
        super(client, new HttpMethod("get"));
    }

    /**
     * Application ID
     */
    public SearchForAccounts applicationId(Long applicationId) {
        addQuery("application-id", String.valueOf(applicationId));
        return this;
    }

    /**
     * Asset ID
     */
    public SearchForAccounts assetId(Long assetId) {
        addQuery("asset-id", String.valueOf(assetId));
        return this;
    }

    /**
     * Include accounts configured to use this spending key.
     */
    public SearchForAccounts authAddr(Address authAddr) {
        addQuery("auth-addr", String.valueOf(authAddr));
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
     * Exclude additional items such as asset holdings, application local data stored
     * for this account, asset parameters created by this account, and application
     * parameters created by this account.
     */
    public SearchForAccounts exclude(List<Enums.Exclude> exclude) {
        addQuery("exclude", StringUtils.join(exclude, ","));
        return this;
    }

    /**
     * Include all items including closed accounts, deleted applications, destroyed
     * assets, opted-out asset holdings, and closed-out application localstates.
     */
    public SearchForAccounts includeAll(Boolean includeAll) {
        addQuery("include-all", String.valueOf(includeAll));
        return this;
    }

    /**
     * Maximum number of results to return. There could be additional pages even if the
     * limit is not reached.
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

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<AccountsResponse> execute() throws Exception {
        Response<AccountsResponse> resp = baseExecute();
        resp.setValueType(AccountsResponse.class);
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
    public Response<AccountsResponse> execute(String[] headers, String[] values) throws Exception {
        Response<AccountsResponse> resp = baseExecute(headers, values);
        resp.setValueType(AccountsResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("accounts"));

        return qd;
    }
}
