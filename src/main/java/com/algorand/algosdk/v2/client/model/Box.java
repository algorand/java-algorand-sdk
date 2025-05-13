package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Box name and its content.
 */
public class Box extends PathResponse {

    /**
     * (name) box name, base64 encoded
     */
    @JsonProperty("name")
    public void name(String base64Encoded) {
        this.name = Encoder.decodeFromBase64(base64Encoded);
    }
    public String name() {
        return Encoder.encodeToBase64(this.name);
    }
    public byte[] name;

    /**
     * The round for which this information is relevant
     */
    @JsonProperty("round")
    public Long round;

    /**
     * (value) box value, base64 encoded.
     */
    @JsonProperty("value")
    public void value(String base64Encoded) {
        this.value = Encoder.decodeFromBase64(base64Encoded);
    }
    public String value() {
        return Encoder.encodeToBase64(this.value);
    }
    public byte[] value;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        Box other = (Box) o;
        if (!Objects.deepEquals(this.name, other.name)) return false;
        if (!Objects.deepEquals(this.round, other.round)) return false;
        if (!Objects.deepEquals(this.value, other.value)) return false;

        return true;
    }
}
