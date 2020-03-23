package com.algorand.algosdk.crypto;

import com.algorand.algosdk.util.Encoder;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;
import java.util.Arrays;

/**
 * A serializable class representing a SHA512-256 Digest
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Digest implements Serializable {
    private static final int DIG_LEN_BYTES  = 32;
    private final byte[] bytes = new byte[DIG_LEN_BYTES];

    /**
     * Create a new digest.
     * @param bytes a length 32 byte array.
     */
    @JsonCreator
    public Digest(byte[] bytes) {
        if (bytes == null) {
            return;
        }
        if (bytes.length != DIG_LEN_BYTES) {
            throw new IllegalArgumentException("digest wrong length");
        }
        System.arraycopy(bytes, 0, this.bytes, 0, DIG_LEN_BYTES);
    }

    /**
     * Create a new digest from a base64 encoded string.
     * @param base64StringDigest base64 encoded string
     */
    @JsonIgnore
    public Digest(String base64StringDigest) {
        this(Encoder.decodeFromBase64(base64StringDigest));
    }

    /**
     * Uninitialized object used for serializer to ignore default values.
     */
    public Digest() {
    }

    @JsonValue
    public byte[] getBytes() {
        return this.bytes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Digest && Arrays.equals(this.bytes, ((Digest)obj).bytes)) {
            return true;
        } else {
            return false;
        }
    }
}
