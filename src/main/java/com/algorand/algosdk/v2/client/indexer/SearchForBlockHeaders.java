package com.algorand.algosdk.v2.client.indexer;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.common.Utils;
import com.algorand.algosdk.v2.client.model.BlockHeadersResponse;


/**
 * Search for block headers. Block headers are returned in ascending round order.
 * Transactions are not included in the output.
 * /v2/block-headers
 */
public class SearchForBlockHeaders extends Query {

    public SearchForBlockHeaders(Client client) {
        super(client, new HttpMethod("get"));
    }

    /**
     * Accounts marked as absent in the block header's participation updates. This
     * parameter accepts a comma separated list of addresses.
     */
    public SearchForBlockHeaders absent(List<Address> absent) {
        addQuery("absent", StringUtils.join(absent, ","));
        return this;
    }

    /**
     * Include results after the given time. Must be an RFC 3339 formatted string.
     */
    public SearchForBlockHeaders afterTime(Date afterTime) {
        addQuery("after-time", Utils.getDateString(afterTime));
        return this;
    }

    /**
     * Include results before the given time. Must be an RFC 3339 formatted string.
     */
    public SearchForBlockHeaders beforeTime(Date beforeTime) {
        addQuery("before-time", Utils.getDateString(beforeTime));
        return this;
    }

    /**
     * Accounts marked as expired in the block header's participation updates. This
     * parameter accepts a comma separated list of addresses.
     */
    public SearchForBlockHeaders expired(List<Address> expired) {
        addQuery("expired", StringUtils.join(expired, ","));
        return this;
    }

    /**
     * Maximum number of results to return. There could be additional pages even if the
     * limit is not reached.
     */
    public SearchForBlockHeaders limit(Long limit) {
        addQuery("limit", String.valueOf(limit));
        return this;
    }

    /**
     * Include results at or before the specified max-round.
     */
    public SearchForBlockHeaders maxRound(Long maxRound) {
        addQuery("max-round", String.valueOf(maxRound));
        return this;
    }

    /**
     * Include results at or after the specified min-round.
     */
    public SearchForBlockHeaders minRound(Long minRound) {
        addQuery("min-round", String.valueOf(minRound));
        return this;
    }

    /**
     * The next page of results. Use the next token provided by the previous results.
     */
    public SearchForBlockHeaders next(String next) {
        addQuery("next", String.valueOf(next));
        return this;
    }

    /**
     * Accounts marked as proposer in the block header's participation updates. This
     * parameter accepts a comma separated list of addresses.
     */
    public SearchForBlockHeaders proposers(List<Address> proposers) {
        addQuery("proposers", StringUtils.join(proposers, ","));
        return this;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<BlockHeadersResponse> execute() throws Exception {
        Response<BlockHeadersResponse> resp = baseExecute();
        resp.setValueType(BlockHeadersResponse.class);
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
    public Response<BlockHeadersResponse> execute(String[] headers, String[] values) throws Exception {
        Response<BlockHeadersResponse> resp = baseExecute(headers, values);
        resp.setValueType(BlockHeadersResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("block-headers"));

        return qd;
    }
}
