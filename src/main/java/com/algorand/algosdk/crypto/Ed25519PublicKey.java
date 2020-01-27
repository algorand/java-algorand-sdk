package com.algorand.algosdk.crypto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;
import java.util.Arrays;

/**
 * a serializable Ed25519PublicKey
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Ed25519PublicKey implements Serializable {
    public static final int KEY_LEN_BYTES  = 32;
    /**
     * The raw bytes.
     */
    private final byte[] bytes = new byte[KEY_LEN_BYTES];

    /**
     * Create a new VRF key.
     * @param bytes a length 32 byte array.
     */
    @JsonCreator
    public Ed25519PublicKey(byte[] bytes) {
        if (bytes == null) {
            return;
        }
        if (bytes.length != KEY_LEN_BYTES) {
            throw new IllegalArgumentException("ed25519 public key wrong length");
        }
        System.arraycopy(bytes, 0, this.bytes, 0, KEY_LEN_BYTES);
    }

    // default values for serializer to ignore
    public Ed25519PublicKey() {
    }

    @JsonValue
    public byte[] getBytes() {
        return this.bytes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Ed25519PublicKey && Arrays.equals(this.bytes, ((Ed25519PublicKey)obj).bytes)) {
            return true;
        } else {
            return false;
        }
    }
}
