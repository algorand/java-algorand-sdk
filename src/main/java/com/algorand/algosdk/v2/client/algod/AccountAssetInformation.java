package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.AccountAssetResponse;


/**
 * Given a specific account public key and asset ID, this call returns the
 * account's asset holding and asset parameters (if either exist). Asset parameters
 * will only be returned if the provided address is the asset's creator.
 * /v2/accounts/{address}/assets/{asset-id}
 */
public class AccountAssetInformation extends Query {

    private Address address;
    private Long assetId;

    /**
     * @param address An account public key
     * @param assetId An asset identifier
     */
    public AccountAssetInformation(Client client, Address address, Long assetId) {
        super(client, new HttpMethod("get"));
        this.address = address;
        this.assetId = assetId;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<AccountAssetResponse> execute() throws Exception {
        Response<AccountAssetResponse> resp = baseExecute();
        resp.setValueType(AccountAssetResponse.class);
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
    public Response<AccountAssetResponse> execute(String[] headers, String[] values) throws Exception {
        Response<AccountAssetResponse> resp = baseExecute(headers, values);
        resp.setValueType(AccountAssetResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (this.address == null) {
            throw new RuntimeException("address is not set. It is a required parameter.");
        }
        if (this.assetId == null) {
            throw new RuntimeException("asset-id is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("accounts"));
        addPathSegment(String.valueOf(address));
        addPathSegment(String.valueOf("assets"));
        addPathSegment(String.valueOf(assetId));

        return qd;
    }
}
