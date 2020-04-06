package com.algorand.algosdk.v2.client.model;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	Block information. Definition: data/bookkeeping/block.go : Block 
 */
public class Block {

	/*
		(gh) hash to which this block belongs. 
	 */
	@JsonProperty("genesis-hash")
	public String genesisHash;

	/*
		(gen) ID to which this block belongs. 
	 */
	@JsonProperty("genesis-id")
	public String genesisId;

	/*
		(prev) Previous block hash. 
	 */
	@JsonProperty("previous-block-hash")
	public String previousBlockHash;

	@JsonProperty("rewards")
	public BlockRewards rewards;

	/*
		(rnd) Current round on which this block was appended to the chain. 
	 */
	@JsonProperty("round")
	public long round;

	/*
		(seed) Sortition seed. 
	 */
	@JsonProperty("seed")
	public String seed;

	/*
		(ts) Block creation timestamp in seconds since eposh 
	 */
	@JsonProperty("timestamp")
	public long timestamp;

	/*
		(txns) list of transactions corresponding to a given round. 
	 */
	@JsonProperty("transactions")
	public List<Transaction> transactions;

	/*
		(txn) TransactionsRoot authenticates the set of transactions appearing in the 
		block. More specifically, it's the root of a merkle tree whose leaves are the 
		block's Txids, in lexicographic order. For the empty block, it's 0. Note that 
		the TxnRoot does not authenticate the signatures on the transactions, only the 
		transactions themselves. Two blocks with the same transactions but in a 
		different order and with different signatures will have the same TxnRoot. 
	 */
	@JsonProperty("transactions-root")
	public String transactionsRoot;

	/*
		(tc) TxnCounter counts the number of transactions committed in the ledger, from 
		the time at which support for this feature was introduced. Specifically, 
		TxnCounter is the number of the next transaction that will be committed after 
		this block. It is 0 when no transactions have ever been committed (since 
		TxnCounter started being supported). 
	 */
	@JsonProperty("txn-counter")
	public long txnCounter;

	@JsonProperty("upgrade-state")
	public BlockUpgradeState upgradeState;

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
		if (!Objects.deepEquals(this.timestamp, other.timestamp)) return false;
		if (!Objects.deepEquals(this.transactions, other.transactions)) return false;
		if (!Objects.deepEquals(this.transactionsRoot, other.transactionsRoot)) return false;
		if (!Objects.deepEquals(this.txnCounter, other.txnCounter)) return false;
		if (!Objects.deepEquals(this.upgradeState, other.upgradeState)) return false;
		if (!Objects.deepEquals(this.upgradeVote, other.upgradeVote)) return false;

		return true;
	}

	@Override
	public String toString() {
		ObjectMapper om = new ObjectMapper(); 
		String jsonStr;
		try {
			jsonStr = om.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
		}
		return jsonStr;
	}
}
