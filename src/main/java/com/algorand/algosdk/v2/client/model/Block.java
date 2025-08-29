package com.algorand.algosdk.v2.client.model;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.crypto.Address;
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
     * the potential bonus payout for this block.
     */
    @JsonProperty("bonus")
    public Long bonus;

    /**
     * the sum of all fees paid by transactions in this block.
     */
    @JsonProperty("fees-collected")
    public Long feesCollected;

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
     * Participation account data that needs to be checked/acted on by the network.
     */
    @JsonProperty("participation-updates")
    public ParticipationUpdates participationUpdates;

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
     * (prev512) Previous block hash, using SHA-512.
     */
    @JsonProperty("previous-block-hash-512")
    public void previousBlockHash512(String base64Encoded) {
        this.previousBlockHash512 = Encoder.decodeFromBase64(base64Encoded);
    }
    public String previousBlockHash512() {
        return Encoder.encodeToBase64(this.previousBlockHash512);
    }
    public byte[] previousBlockHash512;

    /**
     * the proposer of this block.
     */
    @JsonProperty("proposer")
    public void proposer(String proposer) throws NoSuchAlgorithmException {
        this.proposer = new Address(proposer);
    }
    @JsonProperty("proposer")
    public String proposer() throws NoSuchAlgorithmException {
        if (this.proposer != null) {
            return this.proposer.encodeAsString();
        } else {
            return null;
        }
    }
    public Address proposer;

    /**
     * the actual amount transferred to the proposer from the fee sink.
     */
    @JsonProperty("proposer-payout")
    public Long proposerPayout;

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
     * (txn512) TransactionsRootSHA512 is an auxiliary TransactionRoot, built using a
     * vector commitment instead of a merkle tree, and SHA512 hash function instead of
     * the default SHA512_256.
     */
    @JsonProperty("transactions-root-sha512")
    public void transactionsRootSha512(String base64Encoded) {
        this.transactionsRootSha512 = Encoder.decodeFromBase64(base64Encoded);
    }
    public String transactionsRootSha512() {
        return Encoder.encodeToBase64(this.transactionsRootSha512);
    }
    public byte[] transactionsRootSha512;

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
        if (!Objects.deepEquals(this.bonus, other.bonus)) return false;
        if (!Objects.deepEquals(this.feesCollected, other.feesCollected)) return false;
        if (!Objects.deepEquals(this.genesisHash, other.genesisHash)) return false;
        if (!Objects.deepEquals(this.genesisId, other.genesisId)) return false;
        if (!Objects.deepEquals(this.participationUpdates, other.participationUpdates)) return false;
        if (!Objects.deepEquals(this.previousBlockHash, other.previousBlockHash)) return false;
        if (!Objects.deepEquals(this.previousBlockHash512, other.previousBlockHash512)) return false;
        if (!Objects.deepEquals(this.proposer, other.proposer)) return false;
        if (!Objects.deepEquals(this.proposerPayout, other.proposerPayout)) return false;
        if (!Objects.deepEquals(this.rewards, other.rewards)) return false;
        if (!Objects.deepEquals(this.round, other.round)) return false;
        if (!Objects.deepEquals(this.seed, other.seed)) return false;
        if (!Objects.deepEquals(this.stateProofTracking, other.stateProofTracking)) return false;
        if (!Objects.deepEquals(this.timestamp, other.timestamp)) return false;
        if (!Objects.deepEquals(this.transactions, other.transactions)) return false;
        if (!Objects.deepEquals(this.transactionsRoot, other.transactionsRoot)) return false;
        if (!Objects.deepEquals(this.transactionsRootSha256, other.transactionsRootSha256)) return false;
        if (!Objects.deepEquals(this.transactionsRootSha512, other.transactionsRootSha512)) return false;
        if (!Objects.deepEquals(this.txnCounter, other.txnCounter)) return false;
        if (!Objects.deepEquals(this.upgradeState, other.upgradeState)) return false;
        if (!Objects.deepEquals(this.upgradeVote, other.upgradeVote)) return false;

        return true;
    }
}
