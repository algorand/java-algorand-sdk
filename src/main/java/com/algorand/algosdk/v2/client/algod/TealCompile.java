package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.CompileResponse;


/**
 * Given TEAL source code in plain text, return base64 encoded program bytes and
 * base32 SHA512_256 hash of program bytes (Address style). This endpoint is only
 * enabled when a node's configuration file sets EnableDeveloperAPI to true.
 * /v2/teal/compile
 */
public class TealCompile extends Query {

    public TealCompile(Client client) {
        super(client, new HttpMethod("post"));
    }

    /**
     * TEAL source code to be compiled
     */
    public TealCompile source(byte[] source) {
        addToBody(source);
        return this;
    }

    /**
     * When set to `true`, returns the source map of the program as a JSON. Defaults to
     * `false`.
     */
    public TealCompile sourcemap(Boolean sourcemap) {
        addQuery("sourcemap", String.valueOf(sourcemap));
        return this;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<CompileResponse> execute() throws Exception {
        Response<CompileResponse> resp = baseExecute();
        resp.setValueType(CompileResponse.class);
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
    public Response<CompileResponse> execute(String[] headers, String[] values) throws Exception {
        Response<CompileResponse> resp = baseExecute(headers, values);
        resp.setValueType(CompileResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (qd.bodySegments.isEmpty()) {
            throw new RuntimeException("source is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("teal"));
        addPathSegment(String.valueOf("compile"));

        return qd;
    }
}
