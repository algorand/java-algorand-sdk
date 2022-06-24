package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.Box;

import org.apache.commons.lang3.CharSet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


/**
 * Given an application ID and box name, it returns the box name and value (each
 * base64 encoded).
 * /v2/applications/{application-id}/boxes/{box-name}
 */
public class GetApplicationBoxByName extends Query {

    private Long applicationId;
    private byte[] boxName;

    /**
     * @param applicationId An application identifier
     * @param boxName A box name
     */
    public GetApplicationBoxByName(Client client, Long applicationId, byte[] boxName) {
        super(client, new HttpMethod("get"));
        this.applicationId = applicationId;
        this.boxName = boxName;
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
        if (this.boxName == null) {
            throw new RuntimeException("box-name is not set. It is a required parameter.");
        }
        
        try {
            addPathSegment(String.valueOf("v2"));
            addPathSegment(String.valueOf("applications"));
            addPathSegment(String.valueOf(applicationId));
            addPathSegment(String.valueOf("boxes"));
            addPathSegment(Encoder.encodeToURL(boxName));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return qd;
    }
}
