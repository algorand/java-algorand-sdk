package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Fields for asset allocation, re-configuration, and destruction.
 * A zero value for asset-id indicates asset creation.
 * A zero value for the params indicates asset destruction.
 * Definition:
 * data/transactions/asset.go : AssetConfigTxnFields
 */
public class TransactionAssetConfig extends PathResponse {

    /**
     * (xaid) ID of the asset being configured or empty if creating.
     */
    @JsonProperty("asset-id")
    public Long assetId;

    /**
     * AssetParams specifies the parameters for an asset.
     * (apar) when part of an AssetConfig transaction.
     * Definition:
     * data/transactions/asset.go : AssetParams
     */
    @JsonProperty("params")
    public AssetParams params;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        TransactionAssetConfig other = (TransactionAssetConfig) o;
        if (!Objects.deepEquals(this.assetId, other.assetId)) return false;
        if (!Objects.deepEquals(this.params, other.params)) return false;

        return true;
    }
}
