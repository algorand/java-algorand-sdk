package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.PendingTransactionsResponse;


/**
 * Get the list of pending transactions by address, sorted by priority, in
 * decreasing order, truncated at the end at MAX. If MAX = 0, returns all pending
 * transactions.
 * /v2/accounts/{address}/transactions/pending
 */
public class GetPendingTransactionsByAddress extends Query {

    private Address address;

    /**
     * @param address An account public key
     */
    public GetPendingTransactionsByAddress(Client client, Address address) {
        super(client, new HttpMethod("get"));
        addQuery("format", "msgpack");
        this.address = address;
    }

    /**
     * An account public key
     */
    public GetPendingTransactionsByAddress address(Address address) {
        addQuery("address", String.valueOf(address));
        return this;
    }

    /**
     * Truncated number of transactions to display. If max=0, returns all pending txns.
     */
    public GetPendingTransactionsByAddress max(Long max) {
        addQuery("max", String.valueOf(max));
        return this;
    }

    @Override
    public Response<PendingTransactionsResponse> execute() throws Exception {
        Response<PendingTransactionsResponse> resp = baseExecute();
        resp.setValueType(PendingTransactionsResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (!qd.queries.containsKey("address")) {
            throw new RuntimeException("address is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("accounts"));
        addPathSegment(String.valueOf(address));
        addPathSegment(String.valueOf("transactions"));
        addPathSegment(String.valueOf("pending"));

        return qd;
    }
}