package com.algorand.algosdk.v2.client.indexer;

import com.algorand.algosdk.v2.client.common.*;
import com.algorand.algosdk.v2.client.model.BoxesResponse;


/**
 * Given an application ID, returns the box names of that application sorted
 * lexicographically.
 * /v2/applications/{application-id}/boxes
 */
public class SearchForApplicationBoxes extends Query {

    private Long applicationId;

    /**
     * @param applicationId
     */
    public SearchForApplicationBoxes(Client client, Long applicationId) {
        super(client, new HttpMethod("get"));
        this.applicationId = applicationId;
    }

    /**
     * Maximum number of results to return. There could be additional pages even if the
     * limit is not reached.
     */
    public SearchForApplicationBoxes limit(Long limit) {
        addQuery("limit", String.valueOf(limit));
        return this;
    }

    /**
     * The next page of results. Use the next token provided by the previous results.
     */
    public SearchForApplicationBoxes next(String next) {
        addQuery("next", String.valueOf(next));
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
