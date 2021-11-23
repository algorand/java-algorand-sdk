package com.algorand.algosdk.v2.client.model;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.*;

/**
 * Application state delta.
 */
public class AccountStateDelta extends PathResponse {

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
     * Application state delta.
     */
    @JsonProperty("delta")
    public List<EvalDeltaKeyValue> delta = new ArrayList<EvalDeltaKeyValue>();

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        AccountStateDelta other = (AccountStateDelta) o;
        if (!Objects.deepEquals(this.address, other.address)) return false;
        if (!Objects.deepEquals(this.delta, other.delta)) return false;

        return true;
    }
}
