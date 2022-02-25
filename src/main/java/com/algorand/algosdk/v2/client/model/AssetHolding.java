package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Describes an asset held by an account.
 * Definition:
 * data/basics/userBalance.go : AssetHolding
 */
public class AssetHolding extends PathResponse {

    /**
     * (a) number of units held.
     */
    @JsonProperty("amount")
    public java.math.BigInteger amount;

    /**
     * Asset ID of the holding.
     */
    @JsonProperty("asset-id")
    public Long assetId;

    /**
     * Whether or not the asset holding is currently deleted from its account.
     */
    @JsonProperty("deleted")
    public Boolean deleted;

    /**
     * (f) whether or not the holding is frozen.
     */
    @JsonProperty("is-frozen")
    public Boolean isFrozen;

    /**
     * Round during which the account opted into this asset holding.
     */
    @JsonProperty("opted-in-at-round")
    public java.math.BigInteger optedInAtRound;

    /**
     * Round during which the account opted out of this asset holding.
     */
    @JsonProperty("opted-out-at-round")
    public java.math.BigInteger optedOutAtRound;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        AssetHolding other = (AssetHolding) o;
        if (!Objects.deepEquals(this.amount, other.amount)) return false;
        if (!Objects.deepEquals(this.assetId, other.assetId)) return false;
        if (!Objects.deepEquals(this.deleted, other.deleted)) return false;
        if (!Objects.deepEquals(this.isFrozen, other.isFrozen)) return false;
        if (!Objects.deepEquals(this.optedInAtRound, other.optedInAtRound)) return false;
        if (!Objects.deepEquals(this.optedOutAtRound, other.optedOutAtRound)) return false;

        return true;
    }
}
