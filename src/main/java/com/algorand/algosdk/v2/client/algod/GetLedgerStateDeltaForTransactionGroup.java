package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.*;
import com.algorand.algosdk.v2.client.model.LedgerStateDelta;


/**
 * Get a ledger delta for a given transaction group.
 * /v2/deltas/txn/group/{id}
 */
public class GetLedgerStateDeltaForTransactionGroup extends Query {

    private String id;

    /**
     * @param id A transaction ID, or transaction group ID
     */
    public GetLedgerStateDeltaForTransactionGroup(Client client, String id) {
        super(client, new HttpMethod("get"));
        addQuery("format", "msgpack");
        this.id = id;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<LedgerStateDelta> execute() throws Exception {
        Response<LedgerStateDelta> resp = baseExecute();
        resp.setValueType(LedgerStateDelta.class);
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
    public Response<LedgerStateDelta> execute(String[] headers, String[] values) throws Exception {
        Response<LedgerStateDelta> resp = baseExecute(headers, values);
        resp.setValueType(LedgerStateDelta.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (this.id == null) {
            throw new RuntimeException("id is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("deltas"));
        addPathSegment(String.valueOf("txn"));
        addPathSegment(String.valueOf("group"));
        addPathSegment(String.valueOf(id));

        return qd;
    }
}
