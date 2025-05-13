package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.*;
import com.algorand.algosdk.v2.client.model.BlockTxidsResponse;


/**
 * Get the top level transaction IDs for the block on the given round.
 * /v2/blocks/{round}/txids
 */
public class GetBlockTxids extends Query {

    private Long round;

    /**
     * @param round The round from which to fetch block transaction IDs.
     */
    public GetBlockTxids(Client client, Long round) {
        super(client, new HttpMethod("get"));
        this.round = round;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<BlockTxidsResponse> execute() throws Exception {
        Response<BlockTxidsResponse> resp = baseExecute();
        resp.setValueType(BlockTxidsResponse.class);
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
    public Response<BlockTxidsResponse> execute(String[] headers, String[] values) throws Exception {
        Response<BlockTxidsResponse> resp = baseExecute(headers, values);
        resp.setValueType(BlockTxidsResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (this.round == null) {
            throw new RuntimeException("round is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("blocks"));
        addPathSegment(String.valueOf(round));
        addPathSegment(String.valueOf("txids"));

        return qd;
    }
}
