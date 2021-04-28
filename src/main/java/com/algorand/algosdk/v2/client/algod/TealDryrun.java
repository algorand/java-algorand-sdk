package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.DryrunRequest;
import com.algorand.algosdk.v2.client.model.DryrunResponse;
import com.fasterxml.jackson.core.JsonProcessingException;


/**
 * Executes TEAL program(s) in context and returns debugging information about the
 * execution. This endpoint is only enabled when a node's configureation file sets
 * EnableDeveloperAPI to true.
 * /v2/teal/dryrun
 */
public class TealDryrun extends Query {

    public TealDryrun(Client client) {
        super(client, new HttpMethod("post"));
    }

    /**
     * Transaction (or group) and any accompanying state-simulation data.
     * @throws JsonProcessingException
     */
    public TealDryrun request(DryrunRequest request) throws JsonProcessingException {
        addToBody(Encoder.encodeToMsgPack(request));
        return this;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<DryrunResponse> execute() throws Exception {
        Response<DryrunResponse> resp = baseExecute();
        resp.setValueType(DryrunResponse.class);
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
    public Response<DryrunResponse> execute(String[] headers, String[] values) throws Exception {
        Response<DryrunResponse> resp = baseExecute(headers, values);
        resp.setValueType(DryrunResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("teal"));
        addPathSegment(String.valueOf("dryrun"));

        return qd;
    }
}
