package com.algorand.algosdk.v2.client.model;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A simplified version of AssetHolding
 */
public class MiniAssetHolding extends PathResponse {

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

    @JsonProperty("amount")
    public java.math.BigInteger amount;

    /**
     * Whether or not this asset holding is currently deleted from its account.
     */
    @JsonProperty("deleted")
    public Boolean deleted;

    @JsonProperty("is-frozen")
    public Boolean isFrozen;

    /**
     * Round during which the account opted into the asset.
     */
    @JsonProperty("opted-in-at-round")
    public java.math.BigInteger optedInAtRound;

    /**
     * Round during which the account opted out of the asset.
     */
    @JsonProperty("opted-out-at-round")
    public java.math.BigInteger optedOutAtRound;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        MiniAssetHolding other = (MiniAssetHolding) o;
        if (!Objects.deepEquals(this.address, other.address)) return false;
        if (!Objects.deepEquals(this.amount, other.amount)) return false;
        if (!Objects.deepEquals(this.deleted, other.deleted)) return false;
        if (!Objects.deepEquals(this.isFrozen, other.isFrozen)) return false;
        if (!Objects.deepEquals(this.optedInAtRound, other.optedInAtRound)) return false;
        if (!Objects.deepEquals(this.optedOutAtRound, other.optedOutAtRound)) return false;

        return true;
    }
}
