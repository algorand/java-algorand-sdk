package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a TEAL value delta.
 */
public class EvalDelta extends PathResponse {

    /**
     * (at) delta action.
     */
    @JsonProperty("action")
    public Long action;

    /**
     * (bs) bytes value.
     */
    @JsonProperty("bytes")
    public String bytes;

    /**
     * (ui) uint value.
     */
    @JsonProperty("uint")
    public java.math.BigInteger uint;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        EvalDelta other = (EvalDelta) o;
        if (!Objects.deepEquals(this.action, other.action)) return false;
        if (!Objects.deepEquals(this.bytes, other.bytes)) return false;
        if (!Objects.deepEquals(this.uint, other.uint)) return false;

        return true;
    }
}
