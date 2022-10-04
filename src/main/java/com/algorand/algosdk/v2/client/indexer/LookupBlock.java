package com.algorand.algosdk.v2.client.indexer;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.Block;


/**
 * Lookup block.
 * /v2/blocks/{round-number}
 */
public class LookupBlock extends Query {

    private Long roundNumber;

    /**
     * @param roundNumber Round number
     */
    public LookupBlock(Client client, Long roundNumber) {
        super(client, new HttpMethod("get"));
        this.roundNumber = roundNumber;
    }

    /**
     * Header only flag. When this is set to true, returned block does not contain the
     * transactions
     */
    public LookupBlock headerOnly(Boolean headerOnly) {
        addQuery("header-only", String.valueOf(headerOnly));
        return this;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<Block> execute() throws Exception {
        Response<Block> resp = baseExecute();
        resp.setValueType(Block.class);
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
    public Response<Block> execute(String[] headers, String[] values) throws Exception {
        Response<Block> resp = baseExecute(headers, values);
        resp.setValueType(Block.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (this.roundNumber == null) {
            throw new RuntimeException("round-number is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("blocks"));
        addPathSegment(String.valueOf(roundNumber));

        return qd;
    }
}
