package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HashFactory extends PathResponse {

    /**
     * (t)
     */
    @JsonProperty("hash-type")
    public Long hashType;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        HashFactory other = (HashFactory) o;
        if (!Objects.deepEquals(this.hashType, other.hashType)) return false;

        return true;
    }
}
