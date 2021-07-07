package com.algorand.algosdk.v2.client.common;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.model.Account;
import com.fasterxml.jackson.core.JsonProcessingException;

public abstract class Query {
    private Client client;
    private HttpMethod httpMethod;
    protected QueryData qd;

    protected Query(Client client, HttpMethod httpMethod) {
        this.client = client;
        this.httpMethod = httpMethod;
        this.qd = new QueryData();
    }

    protected abstract QueryData getRequestString();

    protected <T>Response<T> baseExecute() throws Exception {
        return baseExecute(null, null);
    }

    protected <T>Response<T> baseExecute(String[] headers, String[] values) throws Exception {

        QueryData qData = this.getRequestString();
        com.squareup.okhttp.Response resp = this.client.executeCall(qData, httpMethod, headers, values);
        if (resp.isSuccessful()) {
            return new Response<T>(resp.code(), null, resp.body().contentType().toString(), resp.body().bytes());
        } else {
            return new Response<T>(resp.code(), resp.body().string(), null, null);
        }
    }

    public String getRequestUrl() {
        return getRequestUrl(client.port, client.host);
    }

    public String getRequestUrl(int port, String host) {
        // Make sure the path is generated by calling getRequestString (at most) once.
        if (qd.pathSegments.size() == 0) {
            this.getRequestString();
        }
        return Client.getHttpUrl(this.qd, port, host).toString();
    }

    protected void addQuery(String key, String value) {
        qd.addQuery(key, value);
    }

    protected void resetPathSegment() {
        qd.resetPathSegments();
    }

    protected void addPathSegment(String segment) {
        qd.addPathSegment(segment);
    }

    protected void addToBody(byte[] content) {
        qd.addToBody(content);
    }

    protected void addToBody(Object content) {
        try {
            qd.addToBody(Encoder.encodeToMsgPack(content));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to encode object.", e);
        }
    }

    public abstract Response<?> execute() throws Exception;
    public abstract Response<?> execute(String[] headers, String[] values) throws Exception;
}
