package com.algorand.algosdk.v2.client.model;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents AppParams and AppLocalStateDelta in deltas
 */
public class AppResourceRecord extends PathResponse {

    /**
     * App account address
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
     * Whether the app was deleted
     */
    @JsonProperty("app-deleted")
    public Boolean appDeleted;

    /**
     * App index
     */
    @JsonProperty("app-index")
    public java.math.BigInteger appIndex;

    /**
     * App local state
     */
    @JsonProperty("app-local-state")
    public ApplicationLocalState appLocalState;

    /**
     * Whether the app local state was deleted
     */
    @JsonProperty("app-local-state-deleted")
    public Boolean appLocalStateDeleted;

    /**
     * App params
     */
    @JsonProperty("app-params")
    public ApplicationParams appParams;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        AppResourceRecord other = (AppResourceRecord) o;
        if (!Objects.deepEquals(this.address, other.address)) return false;
        if (!Objects.deepEquals(this.appDeleted, other.appDeleted)) return false;
        if (!Objects.deepEquals(this.appIndex, other.appIndex)) return false;
        if (!Objects.deepEquals(this.appLocalState, other.appLocalState)) return false;
        if (!Objects.deepEquals(this.appLocalStateDeleted, other.appLocalStateDeleted)) return false;
        if (!Objects.deepEquals(this.appParams, other.appParams)) return false;

        return true;
    }
}
