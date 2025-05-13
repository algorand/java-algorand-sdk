package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents an AVM value.
 */
public class AvmValue extends PathResponse {

    /**
     * bytes value.
     */
    @JsonProperty("bytes")
    public void bytes(String base64Encoded) {
        this.bytes = Encoder.decodeFromBase64(base64Encoded);
    }
    public String bytes() {
        return Encoder.encodeToBase64(this.bytes);
    }
    public byte[] bytes;

    /**
     * value type. Value `1` refers to   bytes  , value `2` refers to   uint64  
     */
    @JsonProperty("type")
    public Long type;

    /**
     * uint value.
     */
    @JsonProperty("uint")
    public java.math.BigInteger uint;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        AvmValue other = (AvmValue) o;
        if (!Objects.deepEquals(this.bytes, other.bytes)) return false;
        if (!Objects.deepEquals(this.type, other.type)) return false;
        if (!Objects.deepEquals(this.uint, other.uint)) return false;

        return true;
    }
}
