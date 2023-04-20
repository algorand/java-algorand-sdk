package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;


/**
 * Sets the timestamp offset (seconds) for blocks in dev mode. Providing an offset
 * of 0 will unset this value and try to use the real clock for the timestamp.
 * /v2/devmode/blocks/offset/{offset}
 */
public class SetBlockTimeStampOffset extends Query {

    private Long offset;

    /**
     * @param offset The timestamp offset for blocks in dev mode.
     */
    public SetBlockTimeStampOffset(Client client, Long offset) {
        super(client, new HttpMethod("post"));
        this.offset = offset;
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
        if (this.offset == null) {
            throw new RuntimeException("offset is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("devmode"));
        addPathSegment(String.valueOf("blocks"));
        addPathSegment(String.valueOf("offset"));
        addPathSegment(String.valueOf(offset));

        return qd;
    }
}
