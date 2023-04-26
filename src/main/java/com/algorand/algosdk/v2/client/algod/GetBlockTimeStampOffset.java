package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.GetBlockTimeStampOffsetResponse;


/**
 * Gets the current timestamp offset.
 * /v2/devmode/blocks/offset
 */
public class GetBlockTimeStampOffset extends Query {

    public GetBlockTimeStampOffset(Client client) {
        super(client, new HttpMethod("get"));
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<GetBlockTimeStampOffsetResponse> execute() throws Exception {
        Response<GetBlockTimeStampOffsetResponse> resp = baseExecute();
        resp.setValueType(GetBlockTimeStampOffsetResponse.class);
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
    public Response<GetBlockTimeStampOffsetResponse> execute(String[] headers, String[] values) throws Exception {
        Response<GetBlockTimeStampOffsetResponse> resp = baseExecute(headers, values);
        resp.setValueType(GetBlockTimeStampOffsetResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("devmode"));
        addPathSegment(String.valueOf("blocks"));
        addPathSegment(String.valueOf("offset"));

        return qd;
    }
}
