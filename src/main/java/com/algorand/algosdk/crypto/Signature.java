package com.algorand.algosdk.crypto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;
import java.util.Arrays;

/**
 * A raw serializable signature class.
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Signature implements Serializable {
    @JsonIgnore
    private static final int ED25519_SIG_SIZE = 64;
    @JsonProperty("bytes")
    private final byte[] bytes = new byte[ED25519_SIG_SIZE];

    /**
     * Create a new Signature wrapping the given bytes.
     */
    @JsonCreator
    public Signature(byte[] rawBytes) {
        if (rawBytes == null) {
            return;
        }
        if (rawBytes.length != ED25519_SIG_SIZE) {
            throw new IllegalArgumentException(String.format("Given signature length is not %s", ED25519_SIG_SIZE));
        }
        System.arraycopy(rawBytes, 0, this.bytes, 0, ED25519_SIG_SIZE);
    }

    // default values for serializer to ignore
    public Signature() {
    }

    @JsonValue
    public byte[] getBytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Signature && Arrays.equals(this.bytes, ((Signature) obj).bytes);
    }
}
