package com.algorand.algosdk.crypto;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

/**
 * A serializable representation of a VRF public key.
 */
public class VRFPublicKey {
    private static final int KEY_LEN_BYTES  = 32;
    /**
     * The raw bytes.
     */
    private final byte[] bytes = new byte[KEY_LEN_BYTES];

    /**
     * Create a new VRF key.
     * @param bytes a length 32 byte array.
     */
    public VRFPublicKey(byte[] bytes) {
        Objects.requireNonNull(bytes, "vrf key must not be null");
        if (bytes.length != KEY_LEN_BYTES) {
            throw new IllegalArgumentException("vrf key wrong length");
        }
        System.arraycopy(bytes, 0, this.bytes, 0, KEY_LEN_BYTES);
    }

    @JsonValue
    public byte[] getBytes() {
        return this.bytes;
    }
}
