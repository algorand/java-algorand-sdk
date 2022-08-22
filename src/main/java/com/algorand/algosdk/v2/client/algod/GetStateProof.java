package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.StateProof;


/**
 * Get a state proof that covers a given round
 * /v2/stateproofs/{round}
 */
public class GetStateProof extends Query {

    private Long round;

    /**
     * @param round The round for which a state proof is desired.
     */
    public GetStateProof(Client client, Long round) {
        super(client, new HttpMethod("get"));
        this.round = round;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<StateProof> execute() throws Exception {
        Response<StateProof> resp = baseExecute();
        resp.setValueType(StateProof.class);
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
    public Response<StateProof> execute(String[] headers, String[] values) throws Exception {
        Response<StateProof> resp = baseExecute(headers, values);
        resp.setValueType(StateProof.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (this.round == null) {
            throw new RuntimeException("round is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("stateproofs"));
        addPathSegment(String.valueOf(round));

        return qd;
    }
}
