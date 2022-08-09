package com.algorand.algosdk.v2.client.model;

import java.util.HashMap;
import java.util.Objects;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a state proof and its corresponding message
 */
public class StateProof extends PathResponse {

    /**
     * Represents the message that the state proofs are attesting to.
     */
    @JsonProperty("Message")
    public HashMap<String,Object> message;

    /**
     * The encoded StateProof for the message.
     */
    @JsonProperty("StateProof")
    public void stateProof(String base64Encoded) {
        this.stateProof = Encoder.decodeFromBase64(base64Encoded);
    }
    public String stateProof() {
        return Encoder.encodeToBase64(this.stateProof);
    }
    public byte[] stateProof;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        StateProof other = (StateProof) o;
        if (!Objects.deepEquals(this.message, other.message)) return false;
        if (!Objects.deepEquals(this.stateProof, other.stateProof)) return false;

        return true;
    }
}
