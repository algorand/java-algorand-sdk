package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.AccountAssetsInformationResponse;


/**
 * Lookup an account's asset holdings.
 * /v2/accounts/{address}/assets
 */
public class AccountAssetsInformation extends Query {

    private Address address;

    /**
     * @param address An account public key.
     */
    public AccountAssetsInformation(Client client, Address address) {
        super(client, new HttpMethod("get"));
        this.address = address;
    }

    /**
     * Maximum number of results to return.
     */
    public AccountAssetsInformation limit(Long limit) {
        addQuery("limit", String.valueOf(limit));
        return this;
    }

    /**
     * The next page of results. Use the next token provided by the previous results.
     */
    public AccountAssetsInformation next(String next) {
        addQuery("next", String.valueOf(next));
        return this;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<AccountAssetsInformationResponse> execute() throws Exception {
        Response<AccountAssetsInformationResponse> resp = baseExecute();
        resp.setValueType(AccountAssetsInformationResponse.class);
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
    public Response<AccountAssetsInformationResponse> execute(String[] headers, String[] values) throws Exception {
        Response<AccountAssetsInformationResponse> resp = baseExecute(headers, values);
        resp.setValueType(AccountAssetsInformationResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (this.address == null) {
            throw new RuntimeException("address is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("accounts"));
        addPathSegment(String.valueOf(address));
        addPathSegment(String.valueOf("assets"));

        return qd;
    }
}
