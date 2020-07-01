package com.algorand.algosdk.v2.client.model;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Application index and its parameters
 */
public class Application extends PathResponse {

    /**
     * Application creator
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
     * (appidx) application index.
     */
    @JsonProperty("id")
    public Long id;

    /**
     * (appparams) application parameters.
     */
    @JsonProperty("params")
    public ApplicationParams params;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        Application other = (Application) o;
        if (!Objects.deepEquals(this.creator, other.creator)) return false;
        if (!Objects.deepEquals(this.id, other.id)) return false;
        if (!Objects.deepEquals(this.params, other.params)) return false;

        return true;
    }
}
