package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StateProofMessage extends PathResponse {

    /**
     * (b)
     */
    @JsonProperty("block-headers-commitment")
    public void blockHeadersCommitment(String base64Encoded) {
        this.blockHeadersCommitment = Encoder.decodeFromBase64(base64Encoded);
    }
    public String blockHeadersCommitment() {
        return Encoder.encodeToBase64(this.blockHeadersCommitment);
    }
    public byte[] blockHeadersCommitment;

    /**
     * (f)
     */
    @JsonProperty("first-attested-round")
    public java.math.BigInteger firstAttestedRound;

    /**
     * (l)
     */
    @JsonProperty("latest-attested-round")
    public java.math.BigInteger latestAttestedRound;

    /**
     * (P)
     */
    @JsonProperty("ln-proven-weight")
    public java.math.BigInteger lnProvenWeight;

    /**
     * (v)
     */
    @JsonProperty("voters-commitment")
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
        if (!Objects.deepEquals(this.latestAttestedRound, other.latestAttestedRound)) return false;
        if (!Objects.deepEquals(this.lnProvenWeight, other.lnProvenWeight)) return false;
        if (!Objects.deepEquals(this.votersCommitment, other.votersCommitment)) return false;

        return true;
    }
}
