package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.*;


/**
 * Sets the minimum sync round on the ledger.
 * /v2/ledger/sync/{round}
 */
public class SetSyncRound extends Query {

    private Long round;

    /**
     * @param round The round for which the deltas are desired.
     */
    public SetSyncRound(Client client, Long round) {
        super(client, new HttpMethod("post"));
        this.round = round;
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
        if (this.round == null) {
            throw new RuntimeException("round is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("ledger"));
        addPathSegment(String.valueOf("sync"));
        addPathSegment(String.valueOf(round));

        return qd;
    }
}
