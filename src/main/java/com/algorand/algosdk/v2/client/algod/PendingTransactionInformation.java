package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;


/**
 * Given a transaction id of a recently submitted transaction, it returns
 * information about it. There are several cases when this might succeed:
 * - transaction committed (committed round > 0) - transaction still in the pool
 * (committed round = 0, pool error = "") - transaction removed from pool due to
 * error (committed round = 0, pool error != "")
 * Or the transaction may have happened sufficiently long ago that the node no
 * longer remembers it, and this will return an error.
 * /v2/transactions/pending/{txid}
 */
public class PendingTransactionInformation extends Query {

    private String txid;

    /**
     * @param txid A transaction id
     */
    public PendingTransactionInformation(Client client, String txid) {
        super(client, new HttpMethod("get"));
        addQuery("format", "msgpack");
        this.txid = txid;
    }

    /**
     * A transaction id
     */
    public PendingTransactionInformation txid(String txid) {
        addQuery("txid", String.valueOf(txid));
        return this;
    }

    @Override
    public Response<PendingTransactionResponse> execute() throws Exception {
        Response<PendingTransactionResponse> resp = baseExecute();
        resp.setValueType(PendingTransactionResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (!qd.queries.containsKey("txid")) {
            throw new RuntimeException("txid is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("transactions"));
        addPathSegment(String.valueOf("pending"));
        addPathSegment(String.valueOf(txid));

        return qd;
    }
}