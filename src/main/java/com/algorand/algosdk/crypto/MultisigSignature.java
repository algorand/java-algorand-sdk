package com.algorand.algosdk.crypto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Serializable raw multisig class.
 */
@JsonPropertyOrder(alphabetic=true)
public class MultisigSignature {
    @JsonProperty("v")
    public final int version;
    @JsonProperty("thr")
    public final int threshold;
    @JsonProperty("subsig")
    public final List<MultisigSubsig> subsigs;

    /**
     * create a multisig signature.
     * @param version required
     * @param threshold required
     * @param subsigs can be empty, or null
     */
    public MultisigSignature(int version, int threshold, List<MultisigSubsig> subsigs) {
        this.version = version;
        this.threshold = threshold;
        this.subsigs = Objects.requireNonNull(subsigs, "subsigs must not be null");
    }

    public MultisigSignature(int version, int threshold) {
        this(version, threshold, new ArrayList<MultisigSubsig>());
    }

    // default values for serializer to ignore
    public MultisigSignature() {
        this.version = 0;
        this.threshold = 0;
        this.subsigs = new ArrayList<>();
    }

    /**
     * Serializable multisig sub-signature
     */
    @JsonPropertyOrder(alphabetic=true)
    public static class MultisigSubsig {
        @JsonProperty("pk")
        public final Ed25519PublicKey key;
        @JsonProperty("s")
        public final Signature sig; // optional

        public MultisigSubsig(Ed25519PublicKey key, Signature sig) {
            this.key = Objects.requireNonNull(key, "public key cannot be null");
            this.sig = sig;
        }

        public MultisigSubsig(Ed25519PublicKey key) {
            this(key, new Signature());
        }

        public MultisigSubsig() { this(new Ed25519PublicKey(), new Signature()); }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MultisigSubsig) {
                MultisigSubsig actual = (MultisigSubsig) obj;
                return key.equals(actual.key) && sig.equals(actual.sig);
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
