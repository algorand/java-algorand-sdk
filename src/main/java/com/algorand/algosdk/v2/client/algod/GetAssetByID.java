package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.Asset;


/**
 * Given a asset id, it returns asset information including creator, name, total
 * supply and special addresses.
 * /v2/assets/{asset-id}
 */
public class GetAssetByID extends Query {

    private Long assetId;

    /**
     * @param assetId An asset identifier
     */
    public GetAssetByID(Client client, Long assetId) {
        super(client, new HttpMethod("get"));
        this.assetId = assetId;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<Asset> execute() throws Exception {
        Response<Asset> resp = baseExecute();
        resp.setValueType(Asset.class);
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
    public Response<Asset> execute(String[] headers, String[] values) throws Exception {
        Response<Asset> resp = baseExecute(headers, values);
        resp.setValueType(Asset.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (this.assetId == null) {
            throw new RuntimeException("asset-id is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("assets"));
        addPathSegment(String.valueOf(assetId));

        return qd;
    }
}