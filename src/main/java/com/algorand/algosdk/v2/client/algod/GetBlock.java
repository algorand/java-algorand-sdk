package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.BlockResponse;


/**
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

    @Override
    public Response<BlockResponse> execute() throws Exception {
        Response<BlockResponse> resp = baseExecute();
        resp.setValueType(BlockResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (!qd.queries.containsKey("round")) {
            throw new RuntimeException("round is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("blocks"));
        addPathSegment(String.valueOf(round));

        return qd;
    }
}