package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.*;
import com.algorand.algosdk.v2.client.model.PostTransactionsResponse;


/**
 * Broadcasts a raw transaction or transaction group to the network.
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

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<PostTransactionsResponse> execute() throws Exception {
        Response<PostTransactionsResponse> resp = baseExecute();
        resp.setValueType(PostTransactionsResponse.class);
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
    public Response<PostTransactionsResponse> execute(String[] headers, String[] values) throws Exception {
        Response<PostTransactionsResponse> resp = baseExecute(headers, values);
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
