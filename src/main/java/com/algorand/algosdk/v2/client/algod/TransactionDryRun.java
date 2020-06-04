package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;


/**
 * /v2/transactions/dryrun 
 */
public class TransactionDryRun extends Query {

    public TransactionDryRun(Client client) {
        super(client, new HttpMethod("post"));
    }

    /**
     * Transaction (or group) and any accompanying state-simulation data 
     */
    public TransactionDryRun rawtxn(byte[] rawtxn) {
        addToBody(rawtxn);
        return this;
    }

    @Override
    public Response<String> execute() throws Exception {
        Response<String> resp = baseExecute();
        resp.setValueType(String.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (qd.bodySegments.isEmpty()) {
            throw new RuntimeException("rawtxn is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("transactions"));
        addPathSegment(String.valueOf("dryrun"));

        return qd;
    }
}