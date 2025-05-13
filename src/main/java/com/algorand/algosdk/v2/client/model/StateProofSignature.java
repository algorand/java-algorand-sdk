package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class StateProofSignature extends PathResponse {

    @JsonProperty("falcon-signature")
    public void falconSignature(String base64Encoded) {
        this.falconSignature = Encoder.decodeFromBase64(base64Encoded);
    }
    public String falconSignature() {
        return Encoder.encodeToBase64(this.falconSignature);
    }
    public byte[] falconSignature;

    @JsonProperty("merkle-array-index")
    public Long merkleArrayIndex;

    @JsonProperty("proof")
    public MerkleArrayProof proof;

    /**
     * (vkey)
     */
    @JsonProperty("verifying-key")
    public void verifyingKey(String base64Encoded) {
        this.verifyingKey = Encoder.decodeFromBase64(base64Encoded);
    }
    public String verifyingKey() {
        return Encoder.encodeToBase64(this.verifyingKey);
    }
    public byte[] verifyingKey;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        StateProofSignature other = (StateProofSignature) o;
        if (!Objects.deepEquals(this.falconSignature, other.falconSignature)) return false;
        if (!Objects.deepEquals(this.merkleArrayIndex, other.merkleArrayIndex)) return false;
        if (!Objects.deepEquals(this.proof, other.proof)) return false;
        if (!Objects.deepEquals(this.verifyingKey, other.verifyingKey)) return false;

        return true;
    }
}
