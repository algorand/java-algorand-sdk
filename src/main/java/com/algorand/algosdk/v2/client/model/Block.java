package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Block information.
 * Definition:
 * data/bookkeeping/block.go : Block
 */
public class Block extends PathResponse {

    /**
     * (gh) hash to which this block belongs.
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
     * (gen) ID to which this block belongs.
     */
    @JsonProperty("genesis-id")
    public String genesisId;

    /**
     * (prev) Previous block hash.
     */
    @JsonProperty("previous-block-hash")
    public void previousBlockHash(String base64Encoded) {
        this.previousBlockHash = Encoder.decodeFromBase64(base64Encoded);
    }
    public String previousBlockHash() {
        return Encoder.encodeToBase64(this.previousBlockHash);
    }
    public byte[] previousBlockHash;

    /**
     * Fields relating to rewards,
     */
    @JsonProperty("rewards")
    public BlockRewards rewards;

    /**
     * (rnd) Current round on which this block was appended to the chain.
     */
    @JsonProperty("round")
    public Long round;

    /**
     * (seed) Sortition seed.
     */
    @JsonProperty("seed")
    public void seed(String base64Encoded) {
        this.seed = Encoder.decodeFromBase64(base64Encoded);
    }
    public String seed() {
        return Encoder.encodeToBase64(this.seed);
    }
    public byte[] seed;

    /**
     * Tracks the status of state proofs.
     */
    @JsonProperty("state-proof-tracking")
    public List<StateProofTracking> stateProofTracking = new ArrayList<StateProofTracking>();

    /**
     * (ts) Block creation timestamp in seconds since eposh
     */
    @JsonProperty("timestamp")
    public Long timestamp;

    /**
     * (txns) list of transactions corresponding to a given round.
     */
    @JsonProperty("transactions")
    public List<Transaction> transactions = new ArrayList<Transaction>();

    /**
     * (txn) TransactionsRoot authenticates the set of transactions appearing in the
     * block. More specifically, it's the root of a merkle tree whose leaves are the
     * block's Txids, in lexicographic order. For the empty block, it's 0. Note that
     * the TxnRoot does not authenticate the signatures on the transactions, only the
     * transactions themselves. Two blocks with the same transactions but in a
     * different order and with different signatures will have the same TxnRoot.
     */
    @JsonProperty("transactions-root")
    public void transactionsRoot(String base64Encoded) {
        this.transactionsRoot = Encoder.decodeFromBase64(base64Encoded);
    }
    public String transactionsRoot() {
        return Encoder.encodeToBase64(this.transactionsRoot);
    }
    public byte[] transactionsRoot;

    /**
     * (txn256) TransactionsRootSHA256 is an auxiliary TransactionRoot, built using a
     * vector commitment instead of a merkle tree, and SHA256 hash function instead of
     * the default SHA512_256. This commitment can be used on environments where only
     * the SHA256 function exists.
     */
    @JsonProperty("transactions-root-sha256")
    public void transactionsRootSha256(String base64Encoded) {
        this.transactionsRootSha256 = Encoder.decodeFromBase64(base64Encoded);
    }
    public String transactionsRootSha256() {
        return Encoder.encodeToBase64(this.transactionsRootSha256);
    }
    public byte[] transactionsRootSha256;

    /**
     * (tc) TxnCounter counts the number of transactions committed in the ledger, from
     * the time at which support for this feature was introduced.
     * Specifically, TxnCounter is the number of the next transaction that will be
     * committed after this block. It is 0 when no transactions have ever been
     * committed (since TxnCounter started being supported).
     */
    @JsonProperty("txn-counter")
    public Long txnCounter;

    /**
     * Fields relating to a protocol upgrade.
     */
    @JsonProperty("upgrade-state")
    public BlockUpgradeState upgradeState;

    /**
     * Fields relating to voting for a protocol upgrade.
     */
    @JsonProperty("upgrade-vote")
    public BlockUpgradeVote upgradeVote;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        Block other = (Block) o;
        if (!Objects.deepEquals(this.genesisHash, other.genesisHash)) return false;
        if (!Objects.deepEquals(this.genesisId, other.genesisId)) return false;
        if (!Objects.deepEquals(this.previousBlockHash, other.previousBlockHash)) return false;
        if (!Objects.deepEquals(this.rewards, other.rewards)) return false;
        if (!Objects.deepEquals(this.round, other.round)) return false;
        if (!Objects.deepEquals(this.seed, other.seed)) return false;
        if (!Objects.deepEquals(this.stateProofTracking, other.stateProofTracking)) return false;
        if (!Objects.deepEquals(this.timestamp, other.timestamp)) return false;
        if (!Objects.deepEquals(this.transactions, other.transactions)) return false;
        if (!Objects.deepEquals(this.transactionsRoot, other.transactionsRoot)) return false;
        if (!Objects.deepEquals(this.transactionsRootSha256, other.transactionsRootSha256)) return false;
        if (!Objects.deepEquals(this.txnCounter, other.txnCounter)) return false;
        if (!Objects.deepEquals(this.upgradeState, other.upgradeState)) return false;
        if (!Objects.deepEquals(this.upgradeVote, other.upgradeVote)) return false;

        return true;
    }
}
