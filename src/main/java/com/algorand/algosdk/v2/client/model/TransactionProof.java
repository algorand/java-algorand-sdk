package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Proof of transaction in a block.
 */
public class TransactionProof extends PathResponse {

    /**
     * The type of hash function used to create the proof, must be one of:
     *   sha512_256
     *   sha256
     */
    @JsonProperty("hashtype")
    public Enums.Hashtype hashtype;

    /**
     * Index of the transaction in the block's payset.
     */
    @JsonProperty("idx")
    public Long idx;

    /**
     * Proof of transaction membership.
     */
    @JsonProperty("proof")
    public void proof(String base64Encoded) {
        this.proof = Encoder.decodeFromBase64(base64Encoded);
    }
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
    public String stibhash() {
        return Encoder.encodeToBase64(this.stibhash);
    }
    public byte[] stibhash;

    /**
     * Represents the depth of the tree that is being proven, i.e. the number of edges
     * from a leaf to the root.
     */
    @JsonProperty("treedepth")
    public Long treedepth;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        TransactionProof other = (TransactionProof) o;
        if (!Objects.deepEquals(this.hashtype, other.hashtype)) return false;
        if (!Objects.deepEquals(this.idx, other.idx)) return false;
        if (!Objects.deepEquals(this.proof, other.proof)) return false;
        if (!Objects.deepEquals(this.stibhash, other.stibhash)) return false;
        if (!Objects.deepEquals(this.treedepth, other.treedepth)) return false;

        return true;
    }
}
