package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.PostTransactionsResponse;


/**
 * Broadcasts a raw transaction to the network.
 * /v2/transactions
 */
public class RawTransaction extends Query {

    public RawTransaction(Client client) {
        super(client, new HttpMethod("post"));
    }

    /**
     * The byte encoded signed transaction to broadcast to network
     */
    public RawTransaction rawtxn(byte[] rawtxn) {
        addToBody(rawtxn);
        return this;
    }

    @Override
    public Response<PostTransactionsResponse> execute() throws Exception {
        Response<PostTransactionsResponse> resp = baseExecute();
        resp.setValueType(PostTransactionsResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (qd.bodySegments.isEmpty()) {
            throw new RuntimeException("rawtxn is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("transactions"));

        return qd;
    }
}