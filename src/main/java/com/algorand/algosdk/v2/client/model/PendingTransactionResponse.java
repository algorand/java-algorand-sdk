package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Given a transaction id of a recently submitted transaction, it returns
 * information about it. There are several cases when this might succeed:
 * - transaction committed (committed round > 0)
 * - transaction still in the pool (committed round = 0, pool error = "")
 * - transaction removed from pool due to error (committed round = 0, pool error !=
 * "")
 * Or the transaction may have happened sufficiently long ago that the node no
 * longer remembers it, and this will return an error.
 */
public class PendingTransactionResponse extends PathResponse {

    /**
     * The application index if the transaction was found and it created an
     * application.
     */
    @JsonProperty("application-index")
    public Long applicationIndex;

    /**
     * The asset index if the transaction was found and it created an asset.
     */
    @JsonProperty("asset-index")
    public Long assetIndex;

    /**
     * Rewards in microalgos applied to the close remainder to account.
     */
    @JsonProperty("close-rewards")
    public Long closeRewards;

    /**
     * Closing amount for the transaction.
     */
    @JsonProperty("closing-amount")
    public Long closingAmount;

    /**
     * The round where this transaction was confirmed, if present.
     */
    @JsonProperty("confirmed-round")
    public Long confirmedRound;

    /**
     * (gd) Global state key/value changes for the application being executed by this
     * transaction.
     */
    @JsonProperty("global-state-delta")
    public List<EvalDeltaKeyValue> globalStateDelta = new ArrayList<EvalDeltaKeyValue>();

    /**
     * (ld) Local state key/value changes for the application being executed by this
     * transaction.
     */
    @JsonProperty("local-state-delta")
    public List<AccountStateDelta> localStateDelta = new ArrayList<AccountStateDelta>();

    /**
     * Indicates that the transaction was kicked out of this node's transaction pool
     * (and specifies why that happened). An empty string indicates the transaction
     * wasn't kicked out of this node's txpool due to an error.
     *
     */
    @JsonProperty("pool-error")
    public String poolError;

    /**
     * Rewards in microalgos applied to the receiver account.
     */
    @JsonProperty("receiver-rewards")
    public Long receiverRewards;

    /**
     * Rewards in microalgos applied to the sender account.
     */
    @JsonProperty("sender-rewards")
    public Long senderRewards;

    /**
     * The raw signed transaction.
     */
    @JsonProperty("txn")
    public SignedTransaction txn;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        PendingTransactionResponse other = (PendingTransactionResponse) o;
        if (!Objects.deepEquals(this.applicationIndex, other.applicationIndex)) return false;
        if (!Objects.deepEquals(this.assetIndex, other.assetIndex)) return false;
        if (!Objects.deepEquals(this.closeRewards, other.closeRewards)) return false;
        if (!Objects.deepEquals(this.closingAmount, other.closingAmount)) return false;
        if (!Objects.deepEquals(this.confirmedRound, other.confirmedRound)) return false;
        if (!Objects.deepEquals(this.globalStateDelta, other.globalStateDelta)) return false;
        if (!Objects.deepEquals(this.localStateDelta, other.localStateDelta)) return false;
        if (!Objects.deepEquals(this.poolError, other.poolError)) return false;
        if (!Objects.deepEquals(this.receiverRewards, other.receiverRewards)) return false;
        if (!Objects.deepEquals(this.senderRewards, other.senderRewards)) return false;
        if (!Objects.deepEquals(this.txn, other.txn)) return false;

        return true;
    }
}
