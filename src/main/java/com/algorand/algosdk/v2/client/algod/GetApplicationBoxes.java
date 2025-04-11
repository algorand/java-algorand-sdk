package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.BoxesResponse;


/**
 * Given an application ID, return boxes in lexographical order by name. If the
 * results must be truncated, a next-token is supplied to continue the request.
 * /v2/applications/{application-id}/boxes
 */
public class GetApplicationBoxes extends Query {

    private Long applicationId;

    /**
     * @param applicationId An application identifier
     */
    public GetApplicationBoxes(Client client, Long applicationId) {
        super(client, new HttpMethod("get"));
        this.applicationId = applicationId;
    }

    /**
     * Maximum number of boxes to return. Server may impose a lower limit.
     */
    public GetApplicationBoxes max(Long max) {
        addQuery("max", String.valueOf(max));
        return this;
    }

    /**
     * A box name, in the goal app call arg form 'encoding:value'. When provided, the
     * returned boxes begin (lexographically) with the supplied name. Callers may
     * implement pagination by reinvoking the endpoint with the token from a previous
     * call's next-token.
     */
    public GetApplicationBoxes next(String next) {
        addQuery("next", String.valueOf(next));
        return this;
    }

    /**
     * A box name prefix, in the goal app call arg form 'encoding:value'. For ints, use
     * the form 'int:1234'. For raw bytes, use the form 'b64:A=='. For printable
     * strings, use the form 'str:hello'. For addresses, use the form 'addr:XYZ...'.
     */
    public GetApplicationBoxes prefix(String prefix) {
        addQuery("prefix", String.valueOf(prefix));
        return this;
    }

    /**
     * If true, box values will be returned.
     */
    public GetApplicationBoxes values(Boolean values) {
        addQuery("values", String.valueOf(values));
        return this;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<BoxesResponse> execute() throws Exception {
        Response<BoxesResponse> resp = baseExecute();
        resp.setValueType(BoxesResponse.class);
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
    public Response<BoxesResponse> execute(String[] headers, String[] values) throws Exception {
        Response<BoxesResponse> resp = baseExecute(headers, values);
        resp.setValueType(BoxesResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (this.applicationId == null) {
            throw new RuntimeException("application-id is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("applications"));
        addPathSegment(String.valueOf(applicationId));
        addPathSegment(String.valueOf("boxes"));

        return qd;
    }
}
