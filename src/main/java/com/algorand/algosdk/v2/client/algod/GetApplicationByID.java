package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.Application;


/**
 * Given a application id, it returns application information including creator,
 * approval and clear programs, global and local schemas, and global state.
 * /v2/applications/{application-id}
 */
public class GetApplicationByID extends Query {

    private Long applicationId;

    /**
     * @param applicationId An application identifier
     */
    public GetApplicationByID(Client client, Long applicationId) {
        super(client, new HttpMethod("get"));
        this.applicationId = applicationId;
    }

    @Override
    public Response<Application> execute() throws Exception {
        Response<Application> resp = baseExecute();
        resp.setValueType(Application.class);
        return resp;
    }

    protected QueryData getRequestString() {
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("applications"));
        addPathSegment(String.valueOf(applicationId));

        return qd;
    }
}