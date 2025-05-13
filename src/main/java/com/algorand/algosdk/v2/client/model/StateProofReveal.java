package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class StateProofReveal extends PathResponse {

    /**
     * (p)
     */
    @JsonProperty("participant")
    public StateProofParticipant participant;

    /**
     * The position in the signature and participants arrays corresponding to this
     * entry.
     */
    @JsonProperty("position")
    public java.math.BigInteger position;

    /**
     * (s)
     */
    @JsonProperty("sig-slot")
    public StateProofSigSlot sigSlot;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        StateProofReveal other = (StateProofReveal) o;
        if (!Objects.deepEquals(this.participant, other.participant)) return false;
        if (!Objects.deepEquals(this.position, other.position)) return false;
        if (!Objects.deepEquals(this.sigSlot, other.sigSlot)) return false;

        return true;
    }
}
