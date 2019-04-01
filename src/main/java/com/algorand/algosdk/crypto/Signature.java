package com.algorand.algosdk.crypto;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

/**
 * A raw serializable signature class.
 */
public class Signature {
    private static final int ED25519_SIG_SIZE = 64;
    private final byte[] bytes = new byte[ED25519_SIG_SIZE];

    /**
     * Create a new Signature wrapping the given bytes.
     */
    public Signature(byte[] rawBytes) {
        Objects.requireNonNull(rawBytes, "rawBytes must not be null");
        if (rawBytes.length != ED25519_SIG_SIZE) {
            throw new IllegalArgumentException(String.format("Given signature length is not %s", ED25519_SIG_SIZE));
        }
        System.arraycopy(rawBytes, 0, this.bytes, 0, ED25519_SIG_SIZE);
    }

    @JsonValue
    public byte[] getBytes() {
        return this.bytes;
    }
}
