package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.BlockHeaderResponse;


/**
 * Get the block header for the block on the given round.
 * /v2/blocks/{round}/header
 */
public class GetBlockHeader extends Query {

    private Long round;

    /**
     * @param round The round from which to fetch block header information.
     */
    public GetBlockHeader(Client client, Long round) {
        super(client, new HttpMethod("get"));
        addQuery("format", "msgpack");
        this.round = round;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<BlockHeaderResponse> execute() throws Exception {
        Response<BlockHeaderResponse> resp = baseExecute();
        resp.setValueType(BlockHeaderResponse.class);
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
    public Response<BlockHeaderResponse> execute(String[] headers, String[] values) throws Exception {
        Response<BlockHeaderResponse> resp = baseExecute(headers, values);
        resp.setValueType(BlockHeaderResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (this.round == null) {
            throw new RuntimeException("round is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("blocks"));
        addPathSegment(String.valueOf(round));
        addPathSegment(String.valueOf("header"));

        return qd;
    }
}
