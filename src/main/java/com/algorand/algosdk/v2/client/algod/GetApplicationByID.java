package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.*;
import com.algorand.algosdk.v2.client.model.Application;


/**
 * Given a application ID, it returns application information including creator,
 * approval and clear programs, global and local schemas, and global state.
 * /v2/applications/{application-id}
 */
public class GetApplicationByID extends Query {

    private Long applicationId;

    /**
     * @param applicationId An application identifier
     */
    public GetApplicationByID(Client client, Long applicationId) {
        super(client, new HttpMethod("get"));
        this.applicationId = applicationId;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<Application> execute() throws Exception {
        Response<Application> resp = baseExecute();
        resp.setValueType(Application.class);
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
    public Response<Application> execute(String[] headers, String[] values) throws Exception {
        Response<Application> resp = baseExecute(headers, values);
        resp.setValueType(Application.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (this.applicationId == null) {
            throw new RuntimeException("application-id is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("applications"));
        addPathSegment(String.valueOf(applicationId));

        return qd;
    }
}
