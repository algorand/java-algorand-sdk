package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Proof of transaction in a block.
 */
public class ProofResponse extends PathResponse {

    /**
     * Index of the transaction in the block's payset.
     */
    @JsonProperty("idx")
    public Long idx;

    /**
     * Merkle proof of transaction membership.
     */
    @JsonProperty("proof")
    public void proof(String base64Encoded) {
        this.proof = Encoder.decodeFromBase64(base64Encoded);
    }
    @JsonProperty("proof")
    public String proof() {
        return Encoder.encodeToBase64(this.proof);
    }
    public byte[] proof;

    /**
     * Hash of SignedTxnInBlock for verifying proof.
     */
    @JsonProperty("stibhash")
    public void stibhash(String base64Encoded) {
        this.stibhash = Encoder.decodeFromBase64(base64Encoded);
    }
    @JsonProperty("stibhash")
    public String stibhash() {
        return Encoder.encodeToBase64(this.stibhash);
    }
    public byte[] stibhash;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        ProofResponse other = (ProofResponse) o;
        if (!Objects.deepEquals(this.idx, other.idx)) return false;
        if (!Objects.deepEquals(this.proof, other.proof)) return false;
        if (!Objects.deepEquals(this.stibhash, other.stibhash)) return false;

        return true;
    }
}
