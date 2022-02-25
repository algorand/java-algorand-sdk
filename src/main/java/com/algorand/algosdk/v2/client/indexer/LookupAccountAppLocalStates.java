package com.algorand.algosdk.v2.client.indexer;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.ApplicationLocalStatesResponse;


/**
 * Lookup an account's asset holdings, optionally for a specific ID.
 * /v2/accounts/{account-id}/apps-local-state
 */
public class LookupAccountAppLocalStates extends Query {

    private Address accountId;

    /**
     * @param accountId account string
     */
    public LookupAccountAppLocalStates(Client client, Address accountId) {
        super(client, new HttpMethod("get"));
        this.accountId = accountId;
    }

    /**
     * Application ID
     */
    public LookupAccountAppLocalStates applicationId(Long applicationId) {
        addQuery("application-id", String.valueOf(applicationId));
        return this;
    }

    /**
     * Include all items including closed accounts, deleted applications, destroyed
     * assets, opted-out asset holdings, and closed-out application localstates.
     */
    public LookupAccountAppLocalStates includeAll(Boolean includeAll) {
        addQuery("include-all", String.valueOf(includeAll));
        return this;
    }

    /**
     * Maximum number of results to return. There could be additional pages even if the
     * limit is not reached.
     */
    public LookupAccountAppLocalStates limit(Long limit) {
        addQuery("limit", String.valueOf(limit));
        return this;
    }

    /**
     * The next page of results. Use the next token provided by the previous results.
     */
    public LookupAccountAppLocalStates next(String next) {
        addQuery("next", String.valueOf(next));
        return this;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<ApplicationLocalStatesResponse> execute() throws Exception {
        Response<ApplicationLocalStatesResponse> resp = baseExecute();
        resp.setValueType(ApplicationLocalStatesResponse.class);
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
    public Response<ApplicationLocalStatesResponse> execute(String[] headers, String[] values) throws Exception {
        Response<ApplicationLocalStatesResponse> resp = baseExecute(headers, values);
        resp.setValueType(ApplicationLocalStatesResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (this.accountId == null) {
            throw new RuntimeException("account-id is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("accounts"));
        addPathSegment(String.valueOf(accountId));
        addPathSegment(String.valueOf("apps-local-state"));

        return qd;
    }
}
