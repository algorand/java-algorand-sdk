package com.algorand.algosdk.v2.client.algod;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.AccountApplicationsInformationResponse;
import com.algorand.algosdk.v2.client.model.Enums;


/**
 * Lookup an account's application holdings (local state and params if the account
 * is the creator).
 * /v2/accounts/{address}/applications
 */
public class AccountApplicationsInformation extends Query {

    private Address address;

    /**
     * @param address An account public key.
     */
    public AccountApplicationsInformation(Client client, Address address) {
        super(client, new HttpMethod("get"));
        this.address = address;
    }

    /**
     * Include additional items in the response. Use `params` to include full
     * application parameters (global state, schema, etc.). Multiple values can be
     * comma-separated. Defaults to returning only application IDs and local state.
     */
    public AccountApplicationsInformation include(List<Enums.Include> include) {
        addQuery("include", StringUtils.join(include, ","));
        return this;
    }

    /**
     * Maximum number of results to return.
     */
    public AccountApplicationsInformation limit(Long limit) {
        addQuery("limit", String.valueOf(limit));
        return this;
    }

    /**
     * The next page of results. Use the next token provided by the previous results.
     */
    public AccountApplicationsInformation next(String next) {
        addQuery("next", String.valueOf(next));
        return this;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<AccountApplicationsInformationResponse> execute() throws Exception {
        Response<AccountApplicationsInformationResponse> resp = baseExecute();
        resp.setValueType(AccountApplicationsInformationResponse.class);
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
    public Response<AccountApplicationsInformationResponse> execute(String[] headers, String[] values) throws Exception {
        Response<AccountApplicationsInformationResponse> resp = baseExecute(headers, values);
        resp.setValueType(AccountApplicationsInformationResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (this.address == null) {
            throw new RuntimeException("address is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("accounts"));
        addPathSegment(String.valueOf(address));
        addPathSegment(String.valueOf("applications"));

        return qd;
    }
}
