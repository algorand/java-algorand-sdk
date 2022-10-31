package com.algorand.algosdk.algod.client.api;

import com.algorand.algosdk.algod.client.AlgodClient;
import com.algorand.algosdk.algod.client.ApiClient;
import com.algorand.algosdk.algod.client.Configuration;

/**
 * @deprecated Use the equivalent in v2 algod client
 */
@Deprecated
public class AlgodApi extends DefaultApi {

    public AlgodApi() {
        super();
    }

    public AlgodApi(ApiClient apiClient) {
        super(apiClient);
    }

}
