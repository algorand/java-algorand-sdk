package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.*;
import com.algorand.algosdk.v2.client.model.BoxesResponse;


/**
 * Given an application ID, return all Box names. No particular ordering is
 * guaranteed. Request fails when client or server-side configured limits prevent
 * returning all Box names.
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
     * Max number of box names to return. If max is not set, or max == 0, returns all
     * box-names.
     */
    public GetApplicationBoxes max(Long max) {
        addQuery("max", String.valueOf(max));
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
