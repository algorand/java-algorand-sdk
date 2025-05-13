package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.*;
import com.algorand.algosdk.v2.client.model.Account;
import com.algorand.algosdk.v2.client.model.Enums;


/**
 * Given a specific account public key, this call returns the account's status,
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

    /**
     * When set to `all` will exclude asset holdings, application local state, created
     * asset parameters, any created application parameters. Defaults to `none`.
     */
    public AccountInformation exclude(Enums.Exclude exclude) {
        addQuery("exclude", String.valueOf(exclude));
        return this;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<Account> execute() throws Exception {
        Response<Account> resp = baseExecute();
        resp.setValueType(Account.class);
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
    public Response<Account> execute(String[] headers, String[] values) throws Exception {
        Response<Account> resp = baseExecute(headers, values);
        resp.setValueType(Account.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (this.address == null) {
            throw new RuntimeException("address is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("accounts"));
        addPathSegment(String.valueOf(address));

        return qd;
    }
}
