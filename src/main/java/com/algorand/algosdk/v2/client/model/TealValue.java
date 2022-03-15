package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a TEAL value.
 */
public class TealValue extends PathResponse {

    /**
     * (tb) bytes value.
     */
    @JsonProperty("bytes")
    public String bytes;

    /**
     * (tt) value type. Value `1` refers to   bytes  , value `2` refers to   uint  
     */
    @JsonProperty("type")
    public Long type;

    /**
     * (ui) uint value.
     */
    @JsonProperty("uint")
    public java.math.BigInteger uint;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        TealValue other = (TealValue) o;
        if (!Objects.deepEquals(this.bytes, other.bytes)) return false;
        if (!Objects.deepEquals(this.type, other.type)) return false;
        if (!Objects.deepEquals(this.uint, other.uint)) return false;

        return true;
    }
}
