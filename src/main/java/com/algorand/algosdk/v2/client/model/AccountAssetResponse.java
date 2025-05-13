package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * AccountAssetResponse describes the account's asset holding and asset parameters
 * (if either exist) for a specific asset ID. Asset parameters will only be
 * returned if the provided address is the asset's creator.
 */
public class AccountAssetResponse extends PathResponse {

    /**
     * (asset) Details about the asset held by this account.
     * The raw account uses `AssetHolding` for this type.
     */
    @JsonProperty("asset-holding")
    public AssetHolding assetHolding;

    /**
     * (apar) parameters of the asset created by this account.
     * The raw account uses `AssetParams` for this type.
     */
    @JsonProperty("created-asset")
    public AssetParams createdAsset;

    /**
     * The round for which this information is relevant.
     */
    @JsonProperty("round")
    public Long round;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        AccountAssetResponse other = (AccountAssetResponse) o;
        if (!Objects.deepEquals(this.assetHolding, other.assetHolding)) return false;
        if (!Objects.deepEquals(this.createdAsset, other.createdAsset)) return false;
        if (!Objects.deepEquals(this.round, other.round)) return false;

        return true;
    }
}
