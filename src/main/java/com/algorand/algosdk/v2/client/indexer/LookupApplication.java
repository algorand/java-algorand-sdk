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
public class LookupApplication extends Query {

    private Long applicationId;

    public LookupApplication(Client client, Long applicationId) {
        super(client, new HttpMethod("get"));
        this.applicationId = applicationId;
    }

    /**
     * Include results for the specified round. 
     */
    public LookupApplication round(Long round) {
        addQuery("round", String.valueOf(round));
        return this;
    }

    @Override
    public Response<ApplicationResponse> execute() throws Exception {
        Response<ApplicationResponse> resp = baseExecute();
        resp.setValueType(ApplicationResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("applications"));
        addPathSegment(String.valueOf(applicationId));

        return qd;
    }
}