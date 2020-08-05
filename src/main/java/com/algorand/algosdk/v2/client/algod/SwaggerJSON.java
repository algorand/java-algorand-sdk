package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;


/**
 * Returns the entire swagger spec in json.
 * /swagger.json
 */
public class SwaggerJSON extends Query {

    public SwaggerJSON(Client client) {
        super(client, new HttpMethod("get"));
    }

    @Override
    public Response<String> execute() throws Exception {
        Response<String> resp = baseExecute();
        resp.setValueType(String.class);
        return resp;
    }

    protected QueryData getRequestString() {
        addPathSegment(String.valueOf("swagger.json"));

        return qd;
    }
}