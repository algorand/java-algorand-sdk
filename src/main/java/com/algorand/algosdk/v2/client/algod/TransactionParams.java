package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;


/**
 * Get parameters for constructing a new transaction
 * /v2/transactions/params
 */
public class TransactionParams extends Query {

    public TransactionParams(Client client) {
        super(client, new HttpMethod("get"));
    }

    @Override
    public Response<TransactionParametersResponse> execute() throws Exception {
        Response<TransactionParametersResponse> resp = baseExecute();
        resp.setValueType(TransactionParametersResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("transactions"));
        addPathSegment(String.valueOf("params"));

        return qd;
    }
}