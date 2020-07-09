package com.algorand.algosdk.v2.client.indexer;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.ApplicationsResponse;


/**
 * Search for applications
 * /v2/applications
 */
public class SearchForApplications extends Query {

    public SearchForApplications(Client client) {
        super(client, new HttpMethod("get"));
    }

    /**
     * Application ID
     */
    public SearchForApplications applicationId(Long applicationId) {
        addQuery("application-id", String.valueOf(applicationId));
        return this;
    }

    @Override
    public Response<ApplicationsResponse> execute() throws Exception {
        Response<ApplicationsResponse> resp = baseExecute();
        resp.setValueType(ApplicationsResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("applications"));

        return qd;
    }
}