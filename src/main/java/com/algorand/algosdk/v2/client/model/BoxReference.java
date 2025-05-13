package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * References a box of an application.
 */
public class BoxReference extends PathResponse {

    /**
     * Application ID which this box belongs to
     */
    @JsonProperty("app")
    public Long app;

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

        BoxReference other = (BoxReference) o;
        if (!Objects.deepEquals(this.app, other.app)) return false;
        if (!Objects.deepEquals(this.name, other.name)) return false;

        return true;
    }
}
