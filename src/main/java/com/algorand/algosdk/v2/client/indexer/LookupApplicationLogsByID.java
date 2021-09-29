package com.algorand.algosdk.v2.client.indexer;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.ApplicationLogsResponse;


/**
 * Lookup application logs.
 * /v2/applications/{application-id}/logs
 */
public class LookupApplicationLogsByID extends Query {

    private Long applicationId;

    /**
     * @param applicationId
     */
    public LookupApplicationLogsByID(Client client, Long applicationId) {
        super(client, new HttpMethod("get"));
        this.applicationId = applicationId;
    }

    /**
     * Maximum number of results to return.
     */
    public LookupApplicationLogsByID limit(Long limit) {
        addQuery("limit", String.valueOf(limit));
        return this;
    }

    /**
     * Include results at or before the specified max-round.
     */
    public LookupApplicationLogsByID maxRound(Long maxRound) {
        addQuery("max-round", String.valueOf(maxRound));
        return this;
    }

    /**
     * Include results at or after the specified min-round.
     */
    public LookupApplicationLogsByID minRound(Long minRound) {
        addQuery("min-round", String.valueOf(minRound));
        return this;
    }

    /**
     * The next page of results. Use the next token provided by the previous results.
     */
    public LookupApplicationLogsByID next(String next) {
        addQuery("next", String.valueOf(next));
        return this;
    }

    /**
     * Only include transactions with this sender address.
     */
    public LookupApplicationLogsByID senderAddress(Address senderAddress) {
        addQuery("sender-address", String.valueOf(senderAddress));
        return this;
    }

    /**
     * Lookup the specific transaction by ID.
     */
    public LookupApplicationLogsByID txid(String txid) {
        addQuery("txid", String.valueOf(txid));
        return this;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<ApplicationLogsResponse> execute() throws Exception {
        Response<ApplicationLogsResponse> resp = baseExecute();
        resp.setValueType(ApplicationLogsResponse.class);
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
    public Response<ApplicationLogsResponse> execute(String[] headers, String[] values) throws Exception {
        Response<ApplicationLogsResponse> resp = baseExecute(headers, values);
        resp.setValueType(ApplicationLogsResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (this.applicationId == null) {
            throw new RuntimeException("application-id is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("applications"));
        addPathSegment(String.valueOf(applicationId));
        addPathSegment(String.valueOf("logs"));

        return qd;
    }
}
