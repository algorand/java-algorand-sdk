package com.algorand.algosdk.v2.client.indexer;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.HealthCheck;


/**
 * Returns 200 if healthy.
 * /health
 */
public class MakeHealthCheck extends Query {

    public MakeHealthCheck(Client client) {
        super(client, new HttpMethod("get"));
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<HealthCheck> execute() throws Exception {
        Response<HealthCheck> resp = baseExecute();
        resp.setValueType(HealthCheck.class);
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
    public Response<HealthCheck> execute(String[] headers, String[] values) throws Exception {
        Response<HealthCheck> resp = baseExecute(headers, values);
        resp.setValueType(HealthCheck.class);
        return resp;
    }

    protected QueryData getRequestString() {
        addPathSegment(String.valueOf("health"));

        return qd;
    }
}
