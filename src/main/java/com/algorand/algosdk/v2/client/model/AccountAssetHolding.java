package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AccountAssetHolding describes the account's asset holding and asset parameters
 * (if either exist) for a specific asset ID.
 */
public class AccountAssetHolding extends PathResponse {

    /**
     * (asset) Details about the asset held by this account.
     * The raw account uses `AssetHolding` for this type.
     */
    @JsonProperty("asset-holding")
    public AssetHolding assetHolding;

    /**
     * (apar) parameters of the asset held by this account.
     * The raw account uses `AssetParams` for this type.
     */
    @JsonProperty("asset-params")
    public AssetParams assetParams;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        AccountAssetHolding other = (AccountAssetHolding) o;
        if (!Objects.deepEquals(this.assetHolding, other.assetHolding)) return false;
        if (!Objects.deepEquals(this.assetParams, other.assetParams)) return false;

        return true;
    }
}
