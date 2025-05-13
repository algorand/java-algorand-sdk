package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.*;
import com.algorand.algosdk.v2.client.model.SimulateRequest;
import com.algorand.algosdk.v2.client.model.SimulateResponse;


/**
 * Simulates a raw transaction or transaction group as it would be evaluated on the
 * network. The simulation will use blockchain state from the latest committed
 * round.
 * /v2/transactions/simulate
 */
public class SimulateTransaction extends Query {

    public SimulateTransaction(Client client) {
        super(client, new HttpMethod("post"));
        addQuery("format", "msgpack");
    }

    /**
     * The transactions to simulate, along with any other inputs.
     */
    public SimulateTransaction request(SimulateRequest request) {
        addToBody(request);
        return this;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<SimulateResponse> execute() throws Exception {
        Response<SimulateResponse> resp = baseExecute();
        resp.setValueType(SimulateResponse.class);
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
    public Response<SimulateResponse> execute(String[] headers, String[] values) throws Exception {
        Response<SimulateResponse> resp = baseExecute(headers, values);
        resp.setValueType(SimulateResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("transactions"));
        addPathSegment(String.valueOf("simulate"));

        return qd;
    }
}
