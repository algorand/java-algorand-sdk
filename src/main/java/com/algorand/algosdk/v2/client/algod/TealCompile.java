package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.CompileResponse;


/**
 * Given TEAL source code in plain text, return base64 encoded program bytes and
 * base32 SHA512_256 hash of program bytes (Address style).
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

    @Override
    public Response<CompileResponse> execute() throws Exception {
        Response<CompileResponse> resp = baseExecute();
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