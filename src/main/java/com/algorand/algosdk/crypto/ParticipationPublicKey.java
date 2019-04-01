package com.algorand.algosdk.crypto;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

/**
 * A serializable class representing a participation key.
 */
public class ParticipationPublicKey {
    private static final int KEY_LEN_BYTES  = 32;
    private final byte[] bytes = new byte[KEY_LEN_BYTES];

    /**
     * Create a new participation key.
     * @param bytes a length 32 byte array.
     */
    public ParticipationPublicKey(byte[] bytes) {
        Objects.requireNonNull(bytes, "participation key must not be null");
        if (bytes.length != KEY_LEN_BYTES) {
            throw new IllegalArgumentException("participation key wrong length");
        }
        System.arraycopy(bytes, 0, this.bytes, 0, KEY_LEN_BYTES);
    }

    @JsonValue
    public byte[] getBytes() {
        return this.bytes;
    }
}
