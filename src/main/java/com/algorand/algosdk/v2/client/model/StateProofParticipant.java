package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StateProofParticipant extends PathResponse {

    /**
     * (p)
     */
    @JsonProperty("verifier")
    public StateProofVerifier verifier;

    /**
     * (w)
     */
    @JsonProperty("weight")
    public java.math.BigInteger weight;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        StateProofParticipant other = (StateProofParticipant) o;
        if (!Objects.deepEquals(this.verifier, other.verifier)) return false;
        if (!Objects.deepEquals(this.weight, other.weight)) return false;

        return true;
    }
}
