package com.algorand.algosdk.transaction;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.ParticipationPublicKey;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class HeartbeatTxnFields implements Serializable {
    @JsonProperty("a")
    public Address hbAddress = new Address();

    @JsonProperty("prf")
    public HeartbeatProof hbProof = new HeartbeatProof();

    @JsonProperty("sd")
    public byte[] hbSeed = new byte[32];  // committee.Seed

    @JsonProperty("vid")
    public ParticipationPublicKey hbVoteID = new ParticipationPublicKey();

    @JsonProperty("kd")
    public BigInteger hbKeyDilution = BigInteger.valueOf(0);

    public HeartbeatTxnFields() {}

    public HeartbeatTxnFields(
            Address hbAddress,
            HeartbeatProof hbProof,
            byte[] hbSeed,
            ParticipationPublicKey hbVoteID,
            BigInteger hbKeyDilution
    ) {
        this.hbAddress = Objects.requireNonNull(hbAddress, "hbAddress must not be null");
        this.hbProof = Objects.requireNonNull(hbProof, "hbProof must not be null");
        this.hbVoteID = Objects.requireNonNull(hbVoteID, "hbVoteID must not be null");
        this.hbKeyDilution = Objects.requireNonNull(hbKeyDilution, "hbKeyDilution must not be null");
        if (hbSeed == null) {
            throw new NullPointerException("hbSeed must not be null");
        }
        System.arraycopy(hbSeed, 0, this.hbSeed, 0, this.hbSeed.length);
    }

    @JsonCreator
    public HeartbeatTxnFields(
            @JsonProperty("a") byte[] hbAddress,
            @JsonProperty("prf") HeartbeatProof hbProof,
            @JsonProperty("sd") byte[] hbSeed,
            @JsonProperty("vid") byte[] hbVoteID,
            @JsonProperty("kd") BigInteger hbKeyDilution
    ) {
        if (hbAddress != null) {
            this.hbAddress = new Address(hbAddress);
        }
        if (hbProof != null) {
            this.hbProof = hbProof;
        }
        if (hbSeed != null) {
            System.arraycopy(hbSeed, 0, this.hbSeed, 0, this.hbSeed.length);
        }
        if (hbVoteID != null) {
            this.hbVoteID = new ParticipationPublicKey(hbVoteID);
        }
        if (hbKeyDilution != null) {
            this.hbKeyDilution = hbKeyDilution;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeartbeatTxnFields that = (HeartbeatTxnFields) o;
        return hbAddress.equals(that.hbAddress) &&
                hbProof.equals(that.hbProof) &&
                Arrays.equals(hbSeed, that.hbSeed) &&
                hbVoteID.equals(that.hbVoteID) &&
                hbKeyDilution.equals(that.hbKeyDilution);
    }
}
