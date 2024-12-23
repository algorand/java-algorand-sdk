package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Fields for a heartbeat transaction.
 * Definition:
 * data/transactions/heartbeat.go : HeartbeatTxnFields
 */
public class TransactionHeartbeat extends PathResponse {

    /**
     * (hbad) HbAddress is the account this txn is proving onlineness for.
     */
    @JsonProperty("hb-address")
    public String hbAddress;

    /**
     * (hbkd) HbKeyDilution must match HbAddress account's current KeyDilution.
     */
    @JsonProperty("hb-key-dilution")
    public java.math.BigInteger hbKeyDilution;

    /**
     * (hbprf) HbProof is a signature using HeartbeatAddress's partkey, thereby showing
     * it is online.
     */
    @JsonProperty("hb-proof")
    public HbProofFields hbProof;

    /**
     * (hbsd) HbSeed must be the block seed for the this transaction's firstValid
     * block.
     */
    @JsonProperty("hb-seed")
    public void hbSeed(String base64Encoded) {
        this.hbSeed = Encoder.decodeFromBase64(base64Encoded);
    }
    public String hbSeed() {
        return Encoder.encodeToBase64(this.hbSeed);
    }
    public byte[] hbSeed;

    /**
     * (hbvid) HbVoteID must match the HbAddress account's current VoteID.
     */
    @JsonProperty("hb-vote-id")
    public void hbVoteId(String base64Encoded) {
        this.hbVoteId = Encoder.decodeFromBase64(base64Encoded);
    }
    public String hbVoteId() {
        return Encoder.encodeToBase64(this.hbVoteId);
    }
    public byte[] hbVoteId;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        TransactionHeartbeat other = (TransactionHeartbeat) o;
        if (!Objects.deepEquals(this.hbAddress, other.hbAddress)) return false;
        if (!Objects.deepEquals(this.hbKeyDilution, other.hbKeyDilution)) return false;
        if (!Objects.deepEquals(this.hbProof, other.hbProof)) return false;
        if (!Objects.deepEquals(this.hbSeed, other.hbSeed)) return false;
        if (!Objects.deepEquals(this.hbVoteId, other.hbVoteId)) return false;

        return true;
    }
}
