package com.algorand.algosdk.v2.client.model;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * HoldingRef names a holding by referring to an Address and Asset it belongs to.
 */
public class HoldingRef extends PathResponse {

    /**
     * (d) Address in access list, or the sender of the transaction.
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
     * (s) Asset ID for asset in access list.
     */
    @JsonProperty("asset")
    public Long asset;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        HoldingRef other = (HoldingRef) o;
        if (!Objects.deepEquals(this.address, other.address)) return false;
        if (!Objects.deepEquals(this.asset, other.asset)) return false;

        return true;
    }
}
