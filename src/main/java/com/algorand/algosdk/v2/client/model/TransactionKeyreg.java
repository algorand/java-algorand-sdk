package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Fields for a keyreg transaction.
 * Definition:
 * data/transactions/keyreg.go : KeyregTxnFields
 */
public class TransactionKeyreg extends PathResponse {

    /**
     * (nonpart) Mark the account as participating or non-participating.
     */
    @JsonProperty("non-participation")
    public Boolean nonParticipation;

    /**
     * (selkey) Public key used with the Verified Random Function (VRF) result during
     * committee selection.
     */
    @JsonProperty("selection-participation-key")
    public void selectionParticipationKey(String base64Encoded) {
        this.selectionParticipationKey = Encoder.decodeFromBase64(base64Encoded);
    }
    public String selectionParticipationKey() {
        return Encoder.encodeToBase64(this.selectionParticipationKey);
    }
    public byte[] selectionParticipationKey;

    /**
     * (sprfkey) State proof key used in key registration transactions.
     */
    @JsonProperty("state-proof-key")
    public void stateProofKey(String base64Encoded) {
        this.stateProofKey = Encoder.decodeFromBase64(base64Encoded);
    }
    public String stateProofKey() {
        return Encoder.encodeToBase64(this.stateProofKey);
    }
    public byte[] stateProofKey;

    /**
     * (votefst) First round this participation key is valid.
     */
    @JsonProperty("vote-first-valid")
    public Long voteFirstValid;

    /**
     * (votekd) Number of subkeys in each batch of participation keys.
     */
    @JsonProperty("vote-key-dilution")
    public Long voteKeyDilution;

    /**
     * (votelst) Last round this participation key is valid.
     */
    @JsonProperty("vote-last-valid")
    public Long voteLastValid;

    /**
     * (votekey) Participation public key used in key registration transactions.
     */
    @JsonProperty("vote-participation-key")
    public void voteParticipationKey(String base64Encoded) {
        this.voteParticipationKey = Encoder.decodeFromBase64(base64Encoded);
    }
    public String voteParticipationKey() {
        return Encoder.encodeToBase64(this.voteParticipationKey);
    }
    public byte[] voteParticipationKey;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        TransactionKeyreg other = (TransactionKeyreg) o;
        if (!Objects.deepEquals(this.nonParticipation, other.nonParticipation)) return false;
        if (!Objects.deepEquals(this.selectionParticipationKey, other.selectionParticipationKey)) return false;
        if (!Objects.deepEquals(this.stateProofKey, other.stateProofKey)) return false;
        if (!Objects.deepEquals(this.voteFirstValid, other.voteFirstValid)) return false;
        if (!Objects.deepEquals(this.voteKeyDilution, other.voteKeyDilution)) return false;
        if (!Objects.deepEquals(this.voteLastValid, other.voteLastValid)) return false;
        if (!Objects.deepEquals(this.voteParticipationKey, other.voteParticipationKey)) return false;

        return true;
    }
}
