package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.*;
import com.algorand.algosdk.v2.client.model.DisassembleResponse;


/**
 * Given the program bytes, return the TEAL source code in plain text. This
 * endpoint is only enabled when a node's configuration file sets
 * EnableDeveloperAPI to true.
 * /v2/teal/disassemble
 */
public class TealDisassemble extends Query {

    public TealDisassemble(Client client) {
        super(client, new HttpMethod("post"));
    }

    /**
     * TEAL program binary to be disassembled
     */
    public TealDisassemble source(byte[] source) {
        addToBody(source);
        return this;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<DisassembleResponse> execute() throws Exception {
        Response<DisassembleResponse> resp = baseExecute();
        resp.setValueType(DisassembleResponse.class);
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
    public Response<DisassembleResponse> execute(String[] headers, String[] values) throws Exception {
        Response<DisassembleResponse> resp = baseExecute(headers, values);
        resp.setValueType(DisassembleResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (qd.bodySegments.isEmpty()) {
            throw new RuntimeException("source is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("teal"));
        addPathSegment(String.valueOf("disassemble"));

        return qd;
    }
}
