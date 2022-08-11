package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Box descriptor describes an app box without a value.
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

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        BoxDescriptor other = (BoxDescriptor) o;
        if (!Objects.deepEquals(this.name, other.name)) return false;

        return true;
    }
}
