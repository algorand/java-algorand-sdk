package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an AVM key-value pair in an application store.
 */
public class AvmKeyValue extends PathResponse {

    @JsonProperty("key")
    public void key(String base64Encoded) {
        this.key = Encoder.decodeFromBase64(base64Encoded);
    }
    public String key() {
        return Encoder.encodeToBase64(this.key);
    }
    public byte[] key;

    /**
     * Represents an AVM value.
     */
    @JsonProperty("value")
    public AvmValue value;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        AvmKeyValue other = (AvmKeyValue) o;
        if (!Objects.deepEquals(this.key, other.key)) return false;
        if (!Objects.deepEquals(this.value, other.value)) return false;

        return true;
    }
}
