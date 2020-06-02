package com.algorand.algosdk.v2.client.indexer;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.HealthCheck;


/**
 * /health 
 */
public class MakeHealthCheck extends Query {

    public MakeHealthCheck(Client client) {
        super(client, new HttpMethod("get"));
    }

    @Override
    public Response<HealthCheck> execute() throws Exception {
        Response<HealthCheck> resp = baseExecute();
        resp.setValueType(HealthCheck.class);
        return resp;
    }

    protected QueryData getRequestString() {
        addPathSegment(String.valueOf("health"));

        return qd;
    }
}