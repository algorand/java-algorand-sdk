package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.Enums;
import com.algorand.algosdk.v2.client.model.TransactionProof;


/**
 * Get a proof for a transaction in a block.
 * /v2/blocks/{round}/transactions/{txid}/proof
 */
public class GetTransactionProof extends Query {

    private Long round;
    private String txid;

    /**
     * @param round A round number.
     * @param txid The transaction ID for which to generate a proof.
     */
    public GetTransactionProof(Client client, Long round, String txid) {
        super(client, new HttpMethod("get"));
        addQuery("format", "msgpack");
        this.round = round;
        this.txid = txid;
    }

    /**
     * The type of hash function used to create the proof, must be one of:
     *   sha512_256
     *   sha256
     */
    public GetTransactionProof hashtype(Enums.Hashtype hashtype) {
        addQuery("hashtype", String.valueOf(hashtype));
        return this;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<TransactionProof> execute() throws Exception {
        Response<TransactionProof> resp = baseExecute();
        resp.setValueType(TransactionProof.class);
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
    public Response<TransactionProof> execute(String[] headers, String[] values) throws Exception {
        Response<TransactionProof> resp = baseExecute(headers, values);
        resp.setValueType(TransactionProof.class);
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
