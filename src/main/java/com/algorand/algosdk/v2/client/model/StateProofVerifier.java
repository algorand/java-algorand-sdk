package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class StateProofVerifier extends PathResponse {

    /**
     * (cmt) Represents the root of the vector commitment tree.
     */
    @JsonProperty("commitment")
    public void commitment(String base64Encoded) {
        this.commitment = Encoder.decodeFromBase64(base64Encoded);
    }
    public String commitment() {
        return Encoder.encodeToBase64(this.commitment);
    }
    public byte[] commitment;

    /**
     * (lf) Key lifetime.
     */
    @JsonProperty("key-lifetime")
    public java.math.BigInteger keyLifetime;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        StateProofVerifier other = (StateProofVerifier) o;
        if (!Objects.deepEquals(this.commitment, other.commitment)) return false;
        if (!Objects.deepEquals(this.keyLifetime, other.keyLifetime)) return false;

        return true;
    }
}
