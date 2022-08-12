package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * (sp) represents a state proof.
 * Definition:
 * crypto/stateproof/structs.go : StateProof
 */
public class StateProof extends PathResponse {

    /**
     * (P)
     */
    @JsonProperty("part-proofs")
    public MerkleArrayProof partProofs;

    /**
     * (pr) Sequence of reveal positions.
     */
    @JsonProperty("positions-to-reveal")
    public List<java.math.BigInteger> positionsToReveal = new ArrayList<java.math.BigInteger>();

    /**
     * (r) Note that this is actually stored as a map[uint64] - Reveal in the actual
     * msgp
     */
    @JsonProperty("reveals")
    public List<StateProofReveal> reveals = new ArrayList<StateProofReveal>();

    /**
     * (v) Salt version of the merkle signature.
     */
    @JsonProperty("salt-version")
    public Long saltVersion;

    /**
     * (c)
     */
    @JsonProperty("sig-commit")
    public void sigCommit(String base64Encoded) {
        this.sigCommit = Encoder.decodeFromBase64(base64Encoded);
    }
    public String sigCommit() {
        return Encoder.encodeToBase64(this.sigCommit);
    }
    public byte[] sigCommit;

    /**
     * (S)
     */
    @JsonProperty("sig-proofs")
    public MerkleArrayProof sigProofs;

    /**
     * (w)
     */
    @JsonProperty("signed-weight")
    public java.math.BigInteger signedWeight;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        StateProof other = (StateProof) o;
        if (!Objects.deepEquals(this.partProofs, other.partProofs)) return false;
        if (!Objects.deepEquals(this.positionsToReveal, other.positionsToReveal)) return false;
        if (!Objects.deepEquals(this.reveals, other.reveals)) return false;
        if (!Objects.deepEquals(this.saltVersion, other.saltVersion)) return false;
        if (!Objects.deepEquals(this.sigCommit, other.sigCommit)) return false;
        if (!Objects.deepEquals(this.sigProofs, other.sigProofs)) return false;
        if (!Objects.deepEquals(this.signedWeight, other.signedWeight)) return false;

        return true;
    }
}
