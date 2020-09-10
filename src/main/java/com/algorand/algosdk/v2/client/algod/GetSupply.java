package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.SupplyResponse;


/**
 * Get the current supply reported by the ledger.
 * /v2/ledger/supply
 */
public class GetSupply extends Query {

    public GetSupply(Client client) {
        super(client, new HttpMethod("get"));
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<SupplyResponse> execute() throws Exception {
        Response<SupplyResponse> resp = baseExecute();
        resp.setValueType(SupplyResponse.class);
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
    public Response<SupplyResponse> execute(String[] headers, String[] values) throws Exception {
        Response<SupplyResponse> resp = baseExecute(headers, values);
        resp.setValueType(SupplyResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("ledger"));
        addPathSegment(String.valueOf("supply"));

        return qd;
    }
}
