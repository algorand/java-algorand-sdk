package com.algorand.algosdk.v2.client.model;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.algorand.algosdk.v2.client.model.AssetParams;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Specifies both the unique identifier and the parameters for an asset
 */
public class Asset extends PathResponse {

    /**
     * Asset creator
     */
    @JsonProperty("creator")
    public void creator(String creator) throws NoSuchAlgorithmException {
        this.creator = new Address(creator);
    }
    @JsonProperty("creator")
    public String creator() throws NoSuchAlgorithmException {
        if (this.creator != null) {
            return this.creator.encodeAsString();
        } else {
            return null;
        }
    }
    public Address creator;

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
        if (!Objects.deepEquals(this.creator, other.creator)) return false;
        if (!Objects.deepEquals(this.index, other.index)) return false;
        if (!Objects.deepEquals(this.params, other.params)) return false;

        return true;
    }
}
