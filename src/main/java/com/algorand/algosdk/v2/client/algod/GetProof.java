package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.Enums;
import com.algorand.algosdk.v2.client.model.ProofResponse;


/**
 * Get a Merkle proof for a transaction in a block.
 * /v2/blocks/{round}/transactions/{txid}/proof
 */
public class GetProof extends Query {

    private Long round;
    private String txid;

    /**
     * @param round The round in which the transaction appears.
     * @param txid The transaction ID for which to generate a proof.
     */
    public GetProof(Client client, Long round, String txid) {
        super(client, new HttpMethod("get"));
        addQuery("format", "msgpack");
        this.round = round;
        this.txid = txid;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<ProofResponse> execute() throws Exception {
        Response<ProofResponse> resp = baseExecute();
        resp.setValueType(ProofResponse.class);
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
    public Response<ProofResponse> execute(String[] headers, String[] values) throws Exception {
        Response<ProofResponse> resp = baseExecute(headers, values);
        resp.setValueType(ProofResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (this.round == null) {
            throw new RuntimeException("round is not set. It is a required parameter.");
        }
        if (this.txid == null) {
            throw new RuntimeException("txid is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("blocks"));
        addPathSegment(String.valueOf(round));
        addPathSegment(String.valueOf("transactions"));
        addPathSegment(String.valueOf(txid));
        addPathSegment(String.valueOf("proof"));

        return qd;
    }
}
