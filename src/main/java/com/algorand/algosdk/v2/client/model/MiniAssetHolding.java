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
        return this.address.encodeAsString();
    }
    public Address address;

    @JsonProperty("amount")
    public java.math.BigInteger amount;

    @JsonProperty("is-frozen")
    public Boolean isFrozen;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        MiniAssetHolding other = (MiniAssetHolding) o;
        if (!Objects.deepEquals(this.address, other.address)) return false;
        if (!Objects.deepEquals(this.amount, other.amount)) return false;
        if (!Objects.deepEquals(this.isFrozen, other.isFrozen)) return false;

        return true;
    }
}
