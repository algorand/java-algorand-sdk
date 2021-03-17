package com.algorand.algosdk.v2.client.indexer;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.ApplicationResponse;


/**
 * Lookup application.
 * /v2/applications/{application-id}
 */
public class LookupApplicationByID extends Query {

    private Long applicationId;

    /**
     * @param applicationId
     */
    public LookupApplicationByID(Client client, Long applicationId) {
        super(client, new HttpMethod("get"));
        this.applicationId = applicationId;
    }

    /**
     * Include all items including closed accounts, deleted applications, destroyed
     * assets, opted-out asset holdings, and closed-out application localstates.
     */
    public LookupApplicationByID includeAll(Boolean includeAll) {
        addQuery("include-all", String.valueOf(includeAll));
        return this;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<ApplicationResponse> execute() throws Exception {
        Response<ApplicationResponse> resp = baseExecute();
        resp.setValueType(ApplicationResponse.class);
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
    public Response<ApplicationResponse> execute(String[] headers, String[] values) throws Exception {
        Response<ApplicationResponse> resp = baseExecute(headers, values);
        resp.setValueType(ApplicationResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (this.applicationId == null) {
            throw new RuntimeException("application-id is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("applications"));
        addPathSegment(String.valueOf(applicationId));

        return qd;
    }
}
