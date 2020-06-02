package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.Account;


/**
 * Given a specific account public key, this call returns the accounts status, 
 * balance and spendable amounts 
 * /v2/accounts/{address} 
 */
public class AccountInformation extends Query {

    private Address address;

    /**
     * @param address An account public key 
     */
    public AccountInformation(Client client, Address address) {
        super(client, new HttpMethod("get"));
        this.address = address;
    }

    @Override
    public Response<Account> execute() throws Exception {
        Response<Account> resp = baseExecute();
        resp.setValueType(Account.class);
        return resp;
    }

    protected QueryData getRequestString() {
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("accounts"));
        addPathSegment(String.valueOf(address));

        return qd;
    }
}