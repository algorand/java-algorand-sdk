package com.algorand.algosdk.v2.client.indexer;

import com.algorand.algosdk.v2.client.common.*;
import com.algorand.algosdk.v2.client.model.Box;


/**
 * Given an application ID and box name, returns base64 encoded box name and value.
 * Box names must be in the goal app call arg form 'encoding:value'. For ints, use
 * the form 'int:1234'. For raw bytes, encode base 64 and use 'b64' prefix as in
 * 'b64:A=='. For printable strings, use the form 'str:hello'. For addresses, use
 * the form 'addr:XYZ...'.
 * /v2/applications/{application-id}/box
 */
public class LookupApplicationBoxByIDAndName extends Query {

    private Long applicationId;

    /**
     * @param applicationId
     */
    public LookupApplicationBoxByIDAndName(Client client, Long applicationId) {
        super(client, new HttpMethod("get"));
        this.applicationId = applicationId;
    }

    /**
     * A box name in goal-arg form 'encoding:value'. For ints, use the form 'int:1234'.
     * For raw bytes, use the form 'b64:A=='. For printable strings, use the form
     * 'str:hello'. For addresses, use the form 'addr:XYZ...'.
     */
    public LookupApplicationBoxByIDAndName name(String name) {
        addQuery("name", String.valueOf(name));
        return this;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<Box> execute() throws Exception {
        Response<Box> resp = baseExecute();
        resp.setValueType(Box.class);
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
    public Response<Box> execute(String[] headers, String[] values) throws Exception {
        Response<Box> resp = baseExecute(headers, values);
        resp.setValueType(Box.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (this.applicationId == null) {
            throw new RuntimeException("application-id is not set. It is a required parameter.");
        }
        if (!qd.queries.containsKey("name")) {
            throw new RuntimeException("name is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("applications"));
        addPathSegment(String.valueOf(applicationId));
        addPathSegment(String.valueOf("box"));

        return qd;
    }
}
