package com.algorand.algosdk.v2.client.algod;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.BoxesResponse;
import com.algorand.algosdk.v2.client.model.Enums;


/**
 * Given an application ID, return all box names. No particular ordering is
 * guaranteed. Request fails when client or server-side configured limits prevent
 * returning all box names.
 * Pagination mode is enabled when any of the following parameters are provided:
 * limit, next, prefix, include, or round. In pagination mode box values can be
 * requested and results are returned in sorted order.
 * To paginate: use the next-token from a previous response as the next parameter
 * in the following request. Pin the round parameter to the round value from the
 * first page's response to ensure consistent results across pages. The server
 * enforces a per-response byte limit, so fewer results than limit may be returned
 * even when more exist; the presence of next-token is the only reliable signal
 * that more data is available.
 * /v2/applications/{application-id}/boxes
 */
public class GetApplicationBoxes extends Query {

    private Long applicationId;

    /**
     * @param applicationId An application identifier.
     */
    public GetApplicationBoxes(Client client, Long applicationId) {
        super(client, new HttpMethod("get"));
        this.applicationId = applicationId;
    }

    /**
     * Include additional items in the response. Use `values` to include box values.
     * Multiple values can be comma-separated.
     */
    public GetApplicationBoxes include(List<Enums.Include> include) {
        addQuery("include", StringUtils.join(include, ","));
        return this;
    }

    /**
     * Maximum number of boxes to return per page.
     */
    public GetApplicationBoxes limit(Long limit) {
        addQuery("limit", String.valueOf(limit));
        return this;
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
     * A box name, in the goal app call arg form 'encoding:value', representing the
     * earliest box name to include in results. Use the next-token from a previous
     * response.
     */
    public GetApplicationBoxes next(String next) {
        addQuery("next", String.valueOf(next));
        return this;
    }

    /**
     * A box name prefix, in the goal app call arg form 'encoding:value', to filter
     * results by. Only boxes whose names start with this prefix will be returned.
     */
    public GetApplicationBoxes prefix(String prefix) {
        addQuery("prefix", String.valueOf(prefix));
        return this;
    }

    /**
     * Return box data from the given round. The round must be within the node's
     * available range.
     */
    public GetApplicationBoxes round(java.math.BigInteger round) {
        addQuery("round", String.valueOf(round));
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
