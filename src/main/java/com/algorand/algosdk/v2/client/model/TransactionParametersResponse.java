package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * TransactionParams contains the parameters that help a client construct a new
 * transaction.
 */
public class TransactionParametersResponse extends PathResponse {

    /**
     * ConsensusVersion indicates the consensus protocol version
     * as of LastRound.
     */
    @JsonProperty("consensus-version")
    public String consensusVersion;

    /**
     * Fee is the suggested transaction fee
     * Fee is in units of micro-Algos per byte.
     * Fee may fall to zero but transactions must still have a fee of
     * at least MinTxnFee for the current network protocol.
     */
    @JsonProperty("fee")
    public Long fee;

    /**
     * GenesisHash is the hash of the genesis block.
     */
    @JsonProperty("genesis-hash")
    public void genesisHash(String base64Encoded) {
        this.genesisHash = Encoder.decodeFromBase64(base64Encoded);
    }
    public String genesisHash() {
        return Encoder.encodeToBase64(this.genesisHash);
    }
    public byte[] genesisHash;

    /**
     * GenesisID is an ID listed in the genesis block.
     */
    @JsonProperty("genesis-id")
    public String genesisId;

    /**
     * LastRound indicates the last round seen
     */
    @JsonProperty("last-round")
    public Long lastRound;

    /**
     * The minimum transaction fee (not per byte) required for the
     * txn to validate for the current network protocol.
     */
    @JsonProperty("min-fee")
    public Long minFee;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        TransactionParametersResponse other = (TransactionParametersResponse) o;
        if (!Objects.deepEquals(this.consensusVersion, other.consensusVersion)) return false;
        if (!Objects.deepEquals(this.fee, other.fee)) return false;
        if (!Objects.deepEquals(this.genesisHash, other.genesisHash)) return false;
        if (!Objects.deepEquals(this.genesisId, other.genesisId)) return false;
        if (!Objects.deepEquals(this.lastRound, other.lastRound)) return false;
        if (!Objects.deepEquals(this.minFee, other.minFee)) return false;

        return true;
    }
}
