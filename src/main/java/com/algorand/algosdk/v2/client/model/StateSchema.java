package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a (apls) local-state or (apgs) global-state schema. These schemas
 * determine how much storage may be used in a local-state or global-state for an
 * application. The more space used, the larger minimum balance must be maintained
 * in the account holding the data.
 */
public class StateSchema extends PathResponse {

    /**
     * Maximum number of TEAL byte slices that may be stored in the key/value store.
     */
    @JsonProperty("num-byte-slice")
    public java.math.BigInteger numByteSlice;

    /**
     * Maximum number of TEAL uints that may be stored in the key/value store.
     */
    @JsonProperty("num-uint")
    public java.math.BigInteger numUint;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        StateSchema other = (StateSchema) o;
        if (!Objects.deepEquals(this.numByteSlice, other.numByteSlice)) return false;
        if (!Objects.deepEquals(this.numUint, other.numUint)) return false;

        return true;
    }
}
