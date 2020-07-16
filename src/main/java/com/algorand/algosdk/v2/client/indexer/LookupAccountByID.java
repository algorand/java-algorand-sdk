package com.algorand.algosdk.v2.client.indexer;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.AccountResponse;


/**
 * Lookup account information.
 * /v2/accounts/{account-id}
 */
public class LookupAccountByID extends Query {

    private Address accountId;

    /**
     * @param accountId account string
     */
    public LookupAccountByID(Client client, Address accountId) {
        super(client, new HttpMethod("get"));
        this.accountId = accountId;
    }

    /**
     * Include results for the specified round.
     */
    public LookupAccountByID round(Long round) {
        addQuery("round", String.valueOf(round));
        return this;
    }

    @Override
    public Response<AccountResponse> execute() throws Exception {
        Response<AccountResponse> resp = baseExecute();
        resp.setValueType(AccountResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (this.accountId == null) {
            throw new RuntimeException("account-id is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("accounts"));
        addPathSegment(String.valueOf(accountId));

        return qd;
    }
}