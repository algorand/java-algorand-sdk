package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StateProofTracking extends PathResponse {

    /**
     * (n) Next round for which we will accept a state proof transaction.
     */
    @JsonProperty("next-round")
    public Long nextRound;

    /**
     * (t) The total number of microalgos held by the online accounts during the
     * StateProof round.
     */
    @JsonProperty("online-total-weight")
    public Long onlineTotalWeight;

    /**
     * State Proof Type. Note the raw object uses map with this as key.
     */
    @JsonProperty("type")
    public java.math.BigInteger type;

    /**
     * (v) Root of a vector commitment containing online accounts that will help sign
     * the proof.
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

        StateProofTracking other = (StateProofTracking) o;
        if (!Objects.deepEquals(this.nextRound, other.nextRound)) return false;
        if (!Objects.deepEquals(this.onlineTotalWeight, other.onlineTotalWeight)) return false;
        if (!Objects.deepEquals(this.type, other.type)) return false;
        if (!Objects.deepEquals(this.votersCommitment, other.votersCommitment)) return false;

        return true;
    }
}
