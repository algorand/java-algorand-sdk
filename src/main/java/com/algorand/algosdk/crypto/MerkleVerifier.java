package com.algorand.algosdk.crypto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;
import java.util.Arrays;

/**
 * A serializable representation of a state proof key.
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class MerkleVerifier implements Serializable {
    private static final int KEY_LEN_BYTES  = 64;
    /**
     * The raw bytes.
     */
    private byte[] bytes = new byte[KEY_LEN_BYTES];

    @JsonCreator
    public MerkleVerifier(byte[] bytes) {
        if (bytes == null) {
            return;
        }
        if (bytes.length != KEY_LEN_BYTES ) {
            throw new IllegalArgumentException("state proof key wrong length");
        }
        System.arraycopy(bytes, 0, this.bytes, 0, KEY_LEN_BYTES);
    }
    // default values for serializer to ignore
    public MerkleVerifier() {
    }

    @JsonValue
    public byte[] getBytes() {
        return this.bytes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MerkleVerifier && Arrays.equals(this.bytes, ((MerkleVerifier)obj).bytes)) {
            return true;
        }
        return false;
    }
}
