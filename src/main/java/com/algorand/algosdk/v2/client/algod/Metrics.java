package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;


/**
 * Return metrics about algod functioning.
 * /metrics
 */
public class Metrics extends Query {

    public Metrics(Client client) {
        super(client, new HttpMethod("get"));
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<String> execute() throws Exception {
        Response<String> resp = baseExecute();
        resp.setValueType(String.class);
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
    public Response<String> execute(String[] headers, String[] values) throws Exception {
        Response<String> resp = baseExecute(headers, values);
        resp.setValueType(String.class);
        return resp;
    }

    protected QueryData getRequestString() {
        addPathSegment(String.valueOf("metrics"));

        return qd;
    }
}
