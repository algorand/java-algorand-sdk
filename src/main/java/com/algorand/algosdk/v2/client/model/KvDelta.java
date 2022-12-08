package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A single Delta containing the key, the previous value and the current value for
 * a single round.
 */
public class KvDelta extends PathResponse {

    /**
     * The key, base64 encoded.
     */
    @JsonProperty("key")
    public void key(String base64Encoded) {
        this.key = Encoder.decodeFromBase64(base64Encoded);
    }
    public String key() {
        return Encoder.encodeToBase64(this.key);
    }
    public byte[] key;

    /**
     * The new value of the KV store entry, base64 encoded.
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

        KvDelta other = (KvDelta) o;
        if (!Objects.deepEquals(this.key, other.key)) return false;
        if (!Objects.deepEquals(this.value, other.value)) return false;

        return true;
    }
}
