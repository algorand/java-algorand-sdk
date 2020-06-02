package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.NodeStatusResponse;


/**
 * /v2/status 
 */
public class GetStatus extends Query {

    public GetStatus(Client client) {
        super(client, new HttpMethod("get"));
    }

    @Override
    public Response<NodeStatusResponse> execute() throws Exception {
        Response<NodeStatusResponse> resp = baseExecute();
        resp.setValueType(NodeStatusResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("status"));

        return qd;
    }
}