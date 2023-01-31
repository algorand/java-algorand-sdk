package com.algorand.algosdk.v2.client.model;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents AssetParams and AssetHolding in deltas
 */
public class AssetResourceRecord extends PathResponse {

    /**
     * Account address of the asset
     */
    @JsonProperty("address")
    public void address(String address) throws NoSuchAlgorithmException {
        this.address = new Address(address);
    }
    @JsonProperty("address")
    public String address() throws NoSuchAlgorithmException {
        if (this.address != null) {
            return this.address.encodeAsString();
        } else {
            return null;
        }
    }
    public Address address;

    /**
     * Whether the asset was deleted
     */
    @JsonProperty("asset-deleted")
    public Boolean assetDeleted;

    /**
     * The asset holding
     */
    @JsonProperty("asset-holding")
    public AssetHolding assetHolding;

    /**
     * Whether the asset holding was deleted
     */
    @JsonProperty("asset-holding-deleted")
    public Boolean assetHoldingDeleted;

    /**
     * Index of the asset
     */
    @JsonProperty("asset-index")
    public java.math.BigInteger assetIndex;

    /**
     * Asset params
     */
    @JsonProperty("asset-params")
    public AssetParams assetParams;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        AssetResourceRecord other = (AssetResourceRecord) o;
        if (!Objects.deepEquals(this.address, other.address)) return false;
        if (!Objects.deepEquals(this.assetDeleted, other.assetDeleted)) return false;
        if (!Objects.deepEquals(this.assetHolding, other.assetHolding)) return false;
        if (!Objects.deepEquals(this.assetHoldingDeleted, other.assetHoldingDeleted)) return false;
        if (!Objects.deepEquals(this.assetIndex, other.assetIndex)) return false;
        if (!Objects.deepEquals(this.assetParams, other.assetParams)) return false;

        return true;
    }
}
