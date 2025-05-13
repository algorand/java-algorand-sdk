package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.*;
import com.algorand.algosdk.v2.client.model.BlockResponse;


/**
 * Get the block for the given round.
 * /v2/blocks/{round}
 */
public class GetBlock extends Query {

    private Long round;

    /**
     * @param round The round from which to fetch block information.
     */
    public GetBlock(Client client, Long round) {
        super(client, new HttpMethod("get"));
        addQuery("format", "msgpack");
        this.round = round;
    }

    /**
     * If true, only the block header (exclusive of payset or certificate) may be
     * included in response.
     */
    public GetBlock headerOnly(Boolean headerOnly) {
        addQuery("header-only", String.valueOf(headerOnly));
        return this;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<BlockResponse> execute() throws Exception {
        Response<BlockResponse> resp = baseExecute();
        resp.setValueType(BlockResponse.class);
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
    public Response<BlockResponse> execute(String[] headers, String[] values) throws Exception {
        Response<BlockResponse> resp = baseExecute(headers, values);
        resp.setValueType(BlockResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (this.round == null) {
            throw new RuntimeException("round is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("blocks"));
        addPathSegment(String.valueOf(round));

        return qd;
    }
}
