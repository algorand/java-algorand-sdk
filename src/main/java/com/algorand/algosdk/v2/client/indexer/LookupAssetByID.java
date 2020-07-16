package com.algorand.algosdk.v2.client.indexer;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.AssetResponse;


/**
 * Lookup asset information.
 * /v2/assets/{asset-id}
 */
public class LookupAssetByID extends Query {

    private Long assetId;

    /**
     * @param assetId
     */
    public LookupAssetByID(Client client, Long assetId) {
        super(client, new HttpMethod("get"));
        this.assetId = assetId;
    }

    @Override
    public Response<AssetResponse> execute() throws Exception {
        Response<AssetResponse> resp = baseExecute();
        resp.setValueType(AssetResponse.class);
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