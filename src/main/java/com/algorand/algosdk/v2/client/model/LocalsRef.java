package com.algorand.algosdk.v2.client.model;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * LocalsRef names a local state by referring to an Address and App it belongs to.
 */
public class LocalsRef extends PathResponse {

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
     * (p) Application ID for app in access list, or zero if referring to the called
     * application.
     */
    @JsonProperty("app")
    public Long app;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        LocalsRef other = (LocalsRef) o;
        if (!Objects.deepEquals(this.address, other.address)) return false;
        if (!Objects.deepEquals(this.app, other.app)) return false;

        return true;
    }
}
