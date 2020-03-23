package com.algorand.algosdk.crypto;

import com.algorand.algosdk.util.Digester;
import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/**
 * MultisigAddress is a convenience class for handling multisignature public identities.
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder(alphabetic = true)
public class MultisigAddress implements Serializable {
    @JsonProperty("version")
    public final int version;
    @JsonProperty("threshold")
    public final int threshold;
    @JsonProperty("publicKeys")
    public final List<Ed25519PublicKey> publicKeys = new ArrayList<>();

    private static final byte[] PREFIX = "MultisigAddr".getBytes(StandardCharsets.UTF_8);

    public MultisigAddress(
            int version,
            int threshold,
            List<Ed25519PublicKey> publicKeys
    ) {
        this.version = version;
        this.threshold = threshold;
        this.publicKeys.addAll(publicKeys);

        if (this.version != 1) {
            throw new IllegalArgumentException("Unknown msig version");
        }

        if (
            this.threshold == 0 ||
            this.publicKeys.size() == 0 ||
            this.threshold > this.publicKeys.size()
        ) {
            throw new IllegalArgumentException("Invalid threshold");
        }
    }

    // workaround for nested JsonValue classes
    @JsonCreator
    private MultisigAddress(
            @JsonProperty("publicKeys") List<byte[]> publicKeys,
            @JsonProperty("version")  int version,
            @JsonProperty("threshold") int threshold
    ) {
        this(version, threshold, toKeys(publicKeys));
    }

    // Helper to convert list of byte[]s to list of Ed25519PublicKeys
    private static List<Ed25519PublicKey> toKeys(List<byte[]> keys) {
        List<Ed25519PublicKey> ret = new ArrayList<>();
        for (byte[] key : keys) {
            ret.add(new Ed25519PublicKey(key));
        }
        return ret;
    }

    /**
     * Convert into an address to more easily represent as a string.
     * @return
     * @throws NoSuchAlgorithmException
     */
    public Address toAddress() throws NoSuchAlgorithmException {
        int numPkBytes = Ed25519PublicKey.KEY_LEN_BYTES * this.publicKeys.size();
        byte[] hashable = new byte[PREFIX.length + 2 + numPkBytes];
        System.arraycopy(PREFIX, 0, hashable, 0, PREFIX.length);
        hashable[PREFIX.length] = (byte)this.version;
        hashable[PREFIX.length+1] = (byte)this.threshold;
        for (int i = 0; i < this.publicKeys.size(); i++) {
            System.arraycopy(
                    this.publicKeys.get(i).getBytes(),
                    0,
                    hashable,
                    PREFIX.length+2+i*Ed25519PublicKey.KEY_LEN_BYTES,
                    Ed25519PublicKey.KEY_LEN_BYTES
            );
        }
        return new Address(Digester.digest(hashable));
    }

    @Override
    public String toString() {
        try {
            return this.toAddress().toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultisigAddress that = (MultisigAddress) o;

        return version == that.version &&
                threshold == that.threshold &&
                publicKeys.equals(that.publicKeys);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, threshold, publicKeys);
    }
}
