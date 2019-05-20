package com.algorand.algosdk.crypto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Serializable raw multisig class.
 */
@JsonPropertyOrder(alphabetic=true)
public class MultisigSignature implements Serializable {
    @JsonProperty("v")
    public int version;
    @JsonProperty("thr")
    public int threshold;
    @JsonProperty("subsig")
    public List<MultisigSubsig> subsigs = new ArrayList<>();

    /**
     * create a multisig signature.
     * @param version required
     * @param threshold required
     * @param subsigs can be empty, or null
     */
    @JsonCreator
    public MultisigSignature(
            @JsonProperty("v") int version,
            @JsonProperty("thr") int threshold,
            @JsonProperty("subsig") List<MultisigSubsig> subsigs
    ) {
        this.version = version;
        this.threshold = threshold;
        this.subsigs = Objects.requireNonNull(subsigs, "subsigs must not be null");
    }

    public MultisigSignature(int version, int threshold) {
        this(version, threshold, new ArrayList<MultisigSubsig>());
    }

    public MultisigSignature() {
    }

    /**
     * Serializable multisig sub-signature
     */
    @JsonPropertyOrder(alphabetic=true)
    public static class MultisigSubsig {
        @JsonProperty("pk")
        public Ed25519PublicKey key = new Ed25519PublicKey();
        @JsonProperty("s")
        public Signature sig = new Signature(); // optional

        // workaround wrapped json values
        @JsonCreator
        public MultisigSubsig(
                @JsonProperty("pk") byte[] key,
                @JsonProperty("s") byte[] sig
        ) {
            if (key != null) this.key = new Ed25519PublicKey(key);
            if (sig != null) this.sig = new Signature(sig);
        }

        public MultisigSubsig(Ed25519PublicKey key, Signature sig) {
            this.key = Objects.requireNonNull(key, "public key cannot be null");
            this.sig = sig;
        }

        public MultisigSubsig(Ed25519PublicKey key) {
            this(key, new Signature());
        }

        public MultisigSubsig() {}

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MultisigSubsig) {
                MultisigSubsig actual = (MultisigSubsig) obj;
                return Arrays.equals(key.getBytes(), actual.key.getBytes())
                        && sig.equals(actual.sig);
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MultisigSignature) {
            MultisigSignature actual = (MultisigSignature) obj;
            if (this.version != actual.version) return false;
            if (this.threshold != actual.threshold) return false;
            return this.subsigs.equals(actual.subsigs);
        } else {
            return false;
        }
    }

}
