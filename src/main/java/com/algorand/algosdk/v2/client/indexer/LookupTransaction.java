package com.algorand.algosdk.v2.client.indexer;

import com.algorand.algosdk.v2.client.common.*;
import com.algorand.algosdk.v2.client.model.TransactionResponse;


/**
 * Lookup a single transaction.
 * /v2/transactions/{txid}
 */
public class LookupTransaction extends Query {

    private String txid;

    /**
     * @param txid
     */
    public LookupTransaction(Client client, String txid) {
        super(client, new HttpMethod("get"));
        this.txid = txid;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<TransactionResponse> execute() throws Exception {
        Response<TransactionResponse> resp = baseExecute();
        resp.setValueType(TransactionResponse.class);
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
    public Response<TransactionResponse> execute(String[] headers, String[] values) throws Exception {
        Response<TransactionResponse> resp = baseExecute(headers, values);
        resp.setValueType(TransactionResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (this.txid == null) {
            throw new RuntimeException("txid is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("transactions"));
        addPathSegment(String.valueOf(txid));

        return qd;
    }
}
