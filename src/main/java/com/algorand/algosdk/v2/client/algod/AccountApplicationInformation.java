package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.*;
import com.algorand.algosdk.v2.client.model.AccountApplicationResponse;


/**
 * Given a specific account public key and application ID, this call returns the
 * account's application local state and global state (AppLocalState and AppParams,
 * if either exists). Global state will only be returned if the provided address is
 * the application's creator.
 * /v2/accounts/{address}/applications/{application-id}
 */
public class AccountApplicationInformation extends Query {

    private Address address;
    private Long applicationId;

    /**
     * @param address An account public key
     * @param applicationId An application identifier
     */
    public AccountApplicationInformation(Client client, Address address, Long applicationId) {
        super(client, new HttpMethod("get"));
        this.address = address;
        this.applicationId = applicationId;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<AccountApplicationResponse> execute() throws Exception {
        Response<AccountApplicationResponse> resp = baseExecute();
        resp.setValueType(AccountApplicationResponse.class);
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
    public Response<AccountApplicationResponse> execute(String[] headers, String[] values) throws Exception {
        Response<AccountApplicationResponse> resp = baseExecute(headers, values);
        resp.setValueType(AccountApplicationResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (this.address == null) {
            throw new RuntimeException("address is not set. It is a required parameter.");
        }
        if (this.applicationId == null) {
            throw new RuntimeException("application-id is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("accounts"));
        addPathSegment(String.valueOf(address));
        addPathSegment(String.valueOf("applications"));
        addPathSegment(String.valueOf(applicationId));

        return qd;
    }
}
