package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.Version;


/**
 * Retrieves the supported API versions, binary build versions, and genesis
 * information.
 * /versions
 */
public class GetVersion extends Query {

    public GetVersion(Client client) {
        super(client, new HttpMethod("get"));
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<Version> execute() throws Exception {
        Response<Version> resp = baseExecute();
        resp.setValueType(Version.class);
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
    public Response<Version> execute(String[] headers, String[] values) throws Exception {
        Response<Version> resp = baseExecute(headers, values);
        resp.setValueType(Version.class);
        return resp;
    }

    protected QueryData getRequestString() {
        addPathSegment(String.valueOf("versions"));

        return qd;
    }
}
