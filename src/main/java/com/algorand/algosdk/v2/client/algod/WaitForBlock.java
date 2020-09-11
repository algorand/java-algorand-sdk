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

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<NodeStatusResponse> execute() throws Exception {
        Response<NodeStatusResponse> resp = baseExecute();
        resp.setValueType(NodeStatusResponse.class);
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
    public Response<NodeStatusResponse> execute(String[] headers, String[] values) throws Exception {
        Response<NodeStatusResponse> resp = baseExecute(headers, values);
        resp.setValueType(NodeStatusResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (this.round == null) {
            throw new RuntimeException("round is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("status"));
        addPathSegment(String.valueOf("wait-for-block-after"));
        addPathSegment(String.valueOf(round));

        return qd;
    }
}
