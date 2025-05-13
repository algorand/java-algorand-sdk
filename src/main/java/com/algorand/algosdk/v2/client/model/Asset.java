package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Specifies both the unique identifier and the parameters for an asset
 */
public class Asset extends PathResponse {

    /**
     * Round during which this asset was created.
     */
    @JsonProperty("created-at-round")
    public java.math.BigInteger createdAtRound;

    /**
     * Whether or not this asset is currently deleted.
     */
    @JsonProperty("deleted")
    public Boolean deleted;

    /**
     * Round during which this asset was destroyed.
     */
    @JsonProperty("destroyed-at-round")
    public java.math.BigInteger destroyedAtRound;

    /**
     * unique asset identifier
     */
    @JsonProperty("index")
    public Long index;

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

        Asset other = (Asset) o;
        if (!Objects.deepEquals(this.createdAtRound, other.createdAtRound)) return false;
        if (!Objects.deepEquals(this.deleted, other.deleted)) return false;
        if (!Objects.deepEquals(this.destroyedAtRound, other.destroyedAtRound)) return false;
        if (!Objects.deepEquals(this.index, other.index)) return false;
        if (!Objects.deepEquals(this.params, other.params)) return false;

        return true;
    }
}
