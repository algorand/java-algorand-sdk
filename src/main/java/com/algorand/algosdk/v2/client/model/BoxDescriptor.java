package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Box descriptor describes an app box.
 */
public class BoxDescriptor extends PathResponse {

    /**
     * Base64 encoded box name
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
     * Base64 encoded box value. Present only when the `values` query parameter is set
     * to true.
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

        BoxDescriptor other = (BoxDescriptor) o;
        if (!Objects.deepEquals(this.name, other.name)) return false;
        if (!Objects.deepEquals(this.value, other.value)) return false;

        return true;
    }
}
