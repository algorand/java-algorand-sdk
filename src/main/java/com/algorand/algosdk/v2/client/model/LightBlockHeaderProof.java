package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Proof of membership and position of a light block header.
 */
public class LightBlockHeaderProof extends PathResponse {

    /**
     * The index of the light block header in the vector commitment tree
     */
    @JsonProperty("index")
    public Long index;

    /**
     * The encoded proof.
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
     * Represents the depth of the tree that is being proven, i.e. the number of edges
     * from a leaf to the root.
     */
    @JsonProperty("treedepth")
    public Long treedepth;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        LightBlockHeaderProof other = (LightBlockHeaderProof) o;
        if (!Objects.deepEquals(this.index, other.index)) return false;
        if (!Objects.deepEquals(this.proof, other.proof)) return false;
        if (!Objects.deepEquals(this.treedepth, other.treedepth)) return false;

        return true;
    }
}
