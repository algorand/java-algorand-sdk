package com.algorand.algosdk.crypto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.security.PublicKey;
import java.util.List;
import java.util.Objects;

/**
 * Serializable raw multisig class.
 */
public class MultisigSignature {
    @JsonProperty("v")
    public final int version;
    @JsonProperty("thr")
    public final int threshold;
    @JsonProperty("subsig")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
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
        this.subsigs = subsigs;
    }

    /**
     * Serializable multisig sub-signature
     */
    public static class MultisigSubsig {
        @JsonProperty("pk")
        public final PublicKey key;
        @JsonProperty("s")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public final Signature sig; // optional

        public MultisigSubsig(PublicKey key, Signature sig) {
            this.key = Objects.requireNonNull(key, "public key cannot be null");
            this.sig = sig;
        }

        public MultisigSubsig(PublicKey key) {
            this(key, null);
        }
    }

}
