package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the message that the state proofs are attesting to.
 */
public class StateProofMessage extends PathResponse {

    /**
     * The vector commitment root on all light block headers within a state proof
     * interval.
     */
    @JsonProperty("BlockHeadersCommitment")
    public void blockHeadersCommitment(String base64Encoded) {
        this.blockHeadersCommitment = Encoder.decodeFromBase64(base64Encoded);
    }
    public String blockHeadersCommitment() {
        return Encoder.encodeToBase64(this.blockHeadersCommitment);
    }
    public byte[] blockHeadersCommitment;

    /**
     * The first round the message attests to.
     */
    @JsonProperty("FirstAttestedRound")
    public java.math.BigInteger firstAttestedRound;

    /**
     * The last round the message attests to.
     */
    @JsonProperty("LastAttestedRound")
    public java.math.BigInteger lastAttestedRound;

    /**
     * An integer value representing the natural log of the proven weight with 16 bits
     * of precision. This value would be used to verify the next state proof.
     */
    @JsonProperty("LnProvenWeight")
    public java.math.BigInteger lnProvenWeight;

    /**
     * The vector commitment root of the top N accounts to sign the next StateProof.
     */
    @JsonProperty("VotersCommitment")
    public void votersCommitment(String base64Encoded) {
        this.votersCommitment = Encoder.decodeFromBase64(base64Encoded);
    }
    public String votersCommitment() {
        return Encoder.encodeToBase64(this.votersCommitment);
    }
    public byte[] votersCommitment;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        StateProofMessage other = (StateProofMessage) o;
        if (!Objects.deepEquals(this.blockHeadersCommitment, other.blockHeadersCommitment)) return false;
        if (!Objects.deepEquals(this.firstAttestedRound, other.firstAttestedRound)) return false;
        if (!Objects.deepEquals(this.lastAttestedRound, other.lastAttestedRound)) return false;
        if (!Objects.deepEquals(this.lnProvenWeight, other.lnProvenWeight)) return false;
        if (!Objects.deepEquals(this.votersCommitment, other.votersCommitment)) return false;

        return true;
    }
}
