package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.LightBlockHeaderProof;


/**
 * Gets a proof for a given light block header inside a state proof commitment
 * /v2/blocks/{round}/lightheader/proof
 */
public class GetLightBlockHeaderProof extends Query {

    private Long round;

    /**
     * @param round The round to which the light block header belongs.
     */
    public GetLightBlockHeaderProof(Client client, Long round) {
        super(client, new HttpMethod("get"));
        this.round = round;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<LightBlockHeaderProof> execute() throws Exception {
        Response<LightBlockHeaderProof> resp = baseExecute();
        resp.setValueType(LightBlockHeaderProof.class);
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
    public Response<LightBlockHeaderProof> execute(String[] headers, String[] values) throws Exception {
        Response<LightBlockHeaderProof> resp = baseExecute(headers, values);
        resp.setValueType(LightBlockHeaderProof.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (this.round == null) {
            throw new RuntimeException("round is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("blocks"));
        addPathSegment(String.valueOf(round));
        addPathSegment(String.valueOf("lightheader"));
        addPathSegment(String.valueOf("proof"));

        return qd;
    }
}
