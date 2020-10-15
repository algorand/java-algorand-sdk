package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.PendingTransactionsResponse;


/**
 * Get the list of pending transactions, sorted by priority, in decreasing order,
 * truncated at the end at MAX. If MAX = 0, returns all pending transactions.
 * /v2/transactions/pending
 */
public class GetPendingTransactions extends Query {

    public GetPendingTransactions(Client client) {
        super(client, new HttpMethod("get"));
        addQuery("format", "msgpack");
    }

    /**
     * Truncated number of transactions to display. If max=0, returns all pending txns.
     */
    public GetPendingTransactions max(Long max) {
        addQuery("max", String.valueOf(max));
        return this;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<PendingTransactionsResponse> execute() throws Exception {
        Response<PendingTransactionsResponse> resp = baseExecute();
        resp.setValueType(PendingTransactionsResponse.class);
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
    public Response<PendingTransactionsResponse> execute(String[] headers, String[] values) throws Exception {
        Response<PendingTransactionsResponse> resp = baseExecute(headers, values);
        resp.setValueType(PendingTransactionsResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("transactions"));
        addPathSegment(String.valueOf("pending"));

        return qd;
    }
}
