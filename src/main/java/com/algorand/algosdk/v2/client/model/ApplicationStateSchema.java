package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Specifies maximums on the number of each type that may be stored.
 */
public class ApplicationStateSchema extends PathResponse {

    /**
     * number of byte slices.
     */
    @JsonProperty("num-byte-slice")
    public Long numByteSlice;

    /**
     * number of uints.
     */
    @JsonProperty("num-uint")
    public Long numUint;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        ApplicationStateSchema other = (ApplicationStateSchema) o;
        if (!Objects.deepEquals(this.numByteSlice, other.numByteSlice)) return false;
        if (!Objects.deepEquals(this.numUint, other.numUint)) return false;

        return true;
    }
}
