package com.algorand.algosdk.v2.client.model;

import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Block information. Definition: data/bookkeeping/block.go : Block 
 */
public class Block extends PathResponse {

	/**
	 * (gh) hash to which this block belongs. 
	 */
	private String genesisHash;
	private boolean genesisHashIsSet;
	@JsonProperty("genesis-hash")
	public void setGenesisHash(String genesisHash){
		this.genesisHash = genesisHash;
		genesisHashIsSet = true;
	}
	@JsonProperty("genesis-hash")
	public String getGenesisHash(){
		return genesisHashIsSet ? genesisHash : null;
	}
	/**
	 * Check if has a value for genesisHash 
	 */	@JsonIgnore
	public boolean hasGenesisHash(){
		return genesisHashIsSet;
	}

	/**
	 * (gen) ID to which this block belongs. 
	 */
	private String genesisId;
	private boolean genesisIdIsSet;
	@JsonProperty("genesis-id")
	public void setGenesisId(String genesisId){
		this.genesisId = genesisId;
		genesisIdIsSet = true;
	}
	@JsonProperty("genesis-id")
	public String getGenesisId(){
		return genesisIdIsSet ? genesisId : null;
	}
	/**
	 * Check if has a value for genesisId 
	 */	@JsonIgnore
	public boolean hasGenesisId(){
		return genesisIdIsSet;
	}

	/**
	 * (prev) Previous block hash. 
	 */
	private String previousBlockHash;
	private boolean previousBlockHashIsSet;
	@JsonProperty("previous-block-hash")
	public void setPreviousBlockHash(String previousBlockHash){
		this.previousBlockHash = previousBlockHash;
		previousBlockHashIsSet = true;
	}
	@JsonProperty("previous-block-hash")
	public String getPreviousBlockHash(){
		return previousBlockHashIsSet ? previousBlockHash : null;
	}
	/**
	 * Check if has a value for previousBlockHash 
	 */	@JsonIgnore
	public boolean hasPreviousBlockHash(){
		return previousBlockHashIsSet;
	}

	private BlockRewards rewards;
	private boolean rewardsIsSet;
	@JsonProperty("rewards")
	public void setRewards(BlockRewards rewards){
		this.rewards = rewards;
		rewardsIsSet = true;
	}
	@JsonProperty("rewards")
	public BlockRewards getRewards(){
		return rewardsIsSet ? rewards : null;
	}
	/**
	 * Check if has a value for rewards 
	 */	@JsonIgnore
	public boolean hasRewards(){
		return rewardsIsSet;
	}

	/**
	 * (rnd) Current round on which this block was appended to the chain. 
	 */
	private long round;
	private boolean roundIsSet;
	@JsonProperty("round")
	public void setRound(long round){
		this.round = round;
		roundIsSet = true;
	}
	@JsonProperty("round")
	public Long getRound(){
		return roundIsSet ? round : null;
	}
	/**
	 * Check if has a value for round 
	 */	@JsonIgnore
	public boolean hasRound(){
		return roundIsSet;
	}

	/**
	 * (seed) Sortition seed. 
	 */
	private String seed;
	private boolean seedIsSet;
	@JsonProperty("seed")
	public void setSeed(String seed){
		this.seed = seed;
		seedIsSet = true;
	}
	@JsonProperty("seed")
	public String getSeed(){
		return seedIsSet ? seed : null;
	}
	/**
	 * Check if has a value for seed 
	 */	@JsonIgnore
	public boolean hasSeed(){
		return seedIsSet;
	}

	/**
	 * (ts) Block creation timestamp in seconds since eposh 
	 */
	private long timestamp;
	private boolean timestampIsSet;
	@JsonProperty("timestamp")
	public void setTimestamp(long timestamp){
		this.timestamp = timestamp;
		timestampIsSet = true;
	}
	@JsonProperty("timestamp")
	public Long getTimestamp(){
		return timestampIsSet ? timestamp : null;
	}
	/**
	 * Check if has a value for timestamp 
	 */	@JsonIgnore
	public boolean hasTimestamp(){
		return timestampIsSet;
	}

	/**
	 * (txns) list of transactions corresponding to a given round. 
	 */
	private List<Transaction> transactions;
	private boolean transactionsIsSet;
	@JsonProperty("transactions")
	public void setTransactions(List<Transaction> transactions){
		this.transactions = transactions;
		transactionsIsSet = true;
	}
	@JsonProperty("transactions")
	public List<Transaction> getTransactions(){
		return transactionsIsSet ? transactions : null;
	}
	/**
	 * Check if has a value for transactions 
	 */	@JsonIgnore
	public boolean hasTransactions(){
		return transactionsIsSet;
	}

	/**
	 * (txn) TransactionsRoot authenticates the set of transactions appearing in the 
	 * block. More specifically, it's the root of a merkle tree whose leaves are the 
	 * block's Txids, in lexicographic order. For the empty block, it's 0. Note that 
	 * the TxnRoot does not authenticate the signatures on the transactions, only the 
	 * transactions themselves. Two blocks with the same transactions but in a 
	 * different order and with different signatures will have the same TxnRoot. 
	 */
	private String transactionsRoot;
	private boolean transactionsRootIsSet;
	@JsonProperty("transactions-root")
	public void setTransactionsRoot(String transactionsRoot){
		this.transactionsRoot = transactionsRoot;
		transactionsRootIsSet = true;
	}
	@JsonProperty("transactions-root")
	public String getTransactionsRoot(){
		return transactionsRootIsSet ? transactionsRoot : null;
	}
	/**
	 * Check if has a value for transactionsRoot 
	 */	@JsonIgnore
	public boolean hasTransactionsRoot(){
		return transactionsRootIsSet;
	}

	/**
	 * (tc) TxnCounter counts the number of transactions committed in the ledger, from 
	 * the time at which support for this feature was introduced. Specifically, 
	 * TxnCounter is the number of the next transaction that will be committed after 
	 * this block. It is 0 when no transactions have ever been committed (since 
	 * TxnCounter started being supported). 
	 */
	private long txnCounter;
	private boolean txnCounterIsSet;
	@JsonProperty("txn-counter")
	public void setTxnCounter(long txnCounter){
		this.txnCounter = txnCounter;
		txnCounterIsSet = true;
	}
	@JsonProperty("txn-counter")
	public Long getTxnCounter(){
		return txnCounterIsSet ? txnCounter : null;
	}
	/**
	 * Check if has a value for txnCounter 
	 */	@JsonIgnore
	public boolean hasTxnCounter(){
		return txnCounterIsSet;
	}

	private BlockUpgradeState upgradeState;
	private boolean upgradeStateIsSet;
	@JsonProperty("upgrade-state")
	public void setUpgradeState(BlockUpgradeState upgradeState){
		this.upgradeState = upgradeState;
		upgradeStateIsSet = true;
	}
	@JsonProperty("upgrade-state")
	public BlockUpgradeState getUpgradeState(){
		return upgradeStateIsSet ? upgradeState : null;
	}
	/**
	 * Check if has a value for upgradeState 
	 */	@JsonIgnore
	public boolean hasUpgradeState(){
		return upgradeStateIsSet;
	}

	private BlockUpgradeVote upgradeVote;
	private boolean upgradeVoteIsSet;
	@JsonProperty("upgrade-vote")
	public void setUpgradeVote(BlockUpgradeVote upgradeVote){
		this.upgradeVote = upgradeVote;
		upgradeVoteIsSet = true;
	}
	@JsonProperty("upgrade-vote")
	public BlockUpgradeVote getUpgradeVote(){
		return upgradeVoteIsSet ? upgradeVote : null;
	}
	/**
	 * Check if has a value for upgradeVote 
	 */	@JsonIgnore
	public boolean hasUpgradeVote(){
		return upgradeVoteIsSet;
	}

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
		if (!Objects.deepEquals(this.timestamp, other.timestamp)) return false;
		if (!Objects.deepEquals(this.transactions, other.transactions)) return false;
		if (!Objects.deepEquals(this.transactionsRoot, other.transactionsRoot)) return false;
		if (!Objects.deepEquals(this.txnCounter, other.txnCounter)) return false;
		if (!Objects.deepEquals(this.upgradeState, other.upgradeState)) return false;
		if (!Objects.deepEquals(this.upgradeVote, other.upgradeVote)) return false;

		return true;
	}
}
