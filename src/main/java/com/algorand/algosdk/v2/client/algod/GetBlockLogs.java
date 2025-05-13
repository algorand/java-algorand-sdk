package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.*;
import com.algorand.algosdk.v2.client.model.BlockLogsResponse;


/**
 * Get all of the logs from outer and inner app calls in the given round
 * /v2/blocks/{round}/logs
 */
public class GetBlockLogs extends Query {

    private Long round;

    /**
     * @param round The round from which to fetch block log information.
     */
    public GetBlockLogs(Client client, Long round) {
        super(client, new HttpMethod("get"));
        this.round = round;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<BlockLogsResponse> execute() throws Exception {
        Response<BlockLogsResponse> resp = baseExecute();
        resp.setValueType(BlockLogsResponse.class);
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
    public Response<BlockLogsResponse> execute(String[] headers, String[] values) throws Exception {
        Response<BlockLogsResponse> resp = baseExecute(headers, values);
        resp.setValueType(BlockLogsResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (this.round == null) {
            throw new RuntimeException("round is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("blocks"));
        addPathSegment(String.valueOf(round));
        addPathSegment(String.valueOf("logs"));

        return qd;
    }
}
