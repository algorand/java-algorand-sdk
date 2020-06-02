package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.NodeStatusResponse;


/**
 * Waits for a block to appear after round {round} and returns the node's status at 
 * the time. 
 * /v2/status/wait-for-block-after/{round} 
 */
public class WaitForBlock extends Query {

    private Long round;

    /**
     * @param round The round to wait until returning status 
     */
    public WaitForBlock(Client client, Long round) {
        super(client, new HttpMethod("get"));
        this.round = round;
    }

    @Override
    public Response<NodeStatusResponse> execute() throws Exception {
        Response<NodeStatusResponse> resp = baseExecute();
        resp.setValueType(NodeStatusResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("status"));
        addPathSegment(String.valueOf("wait-for-block-after"));
        addPathSegment(String.valueOf(round));

        return qd;
    }
}