package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StateProofSigSlot extends PathResponse {

    /**
     * (l) The total weight of signatures in the lower-numbered slots.
     */
    @JsonProperty("lower-sig-weight")
    public java.math.BigInteger lowerSigWeight;

    @JsonProperty("signature")
    public StateProofSignature signature;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        StateProofSigSlot other = (StateProofSigSlot) o;
        if (!Objects.deepEquals(this.lowerSigWeight, other.lowerSigWeight)) return false;
        if (!Objects.deepEquals(this.signature, other.signature)) return false;

        return true;
    }
}
