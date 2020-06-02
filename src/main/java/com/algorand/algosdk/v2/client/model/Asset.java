package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Specifies both the unique identifier and the parameters for an asset 
 */
public class Asset extends PathResponse {

    /**
     * unique asset identifier 
     */
    @JsonProperty("index")
    public Long index;

    @JsonProperty("params")
    public AssetParams params;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        Asset other = (Asset) o;
        if (!Objects.deepEquals(this.index, other.index)) return false;
        if (!Objects.deepEquals(this.params, other.params)) return false;

        return true;
    }
}
