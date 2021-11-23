package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.*;

public class AssetResponse extends PathResponse {

    /**
     * Specifies both the unique identifier and the parameters for an asset
     */
    @JsonProperty("asset")
    public Asset asset;

    /**
     * Round at which the results were computed.
     */
    @JsonProperty("current-round")
    public Long currentRound;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        AssetResponse other = (AssetResponse) o;
        if (!Objects.deepEquals(this.asset, other.asset)) return false;
        if (!Objects.deepEquals(this.currentRound, other.currentRound)) return false;

        return true;
    }
}
