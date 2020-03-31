package com.algorand.indexer.schemas;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Block {

	@JsonProperty("timestamp")
	public long timestamp;

	@JsonProperty("genesis-hash")
	public String genesisHash;

	@JsonProperty("previous-block-hash")
	public String previousBlockHash;

	@JsonProperty("upgrade-state")
	public BlockUpgradeState upgradeState;

	@JsonProperty("transactions")
	public List<Transaction> transactions;

	@JsonProperty("txn-counter")
	public long txnCounter;

	@JsonProperty("rewards")
	public BlockRewards rewards;

	@JsonProperty("seed")
	public String seed;

	@JsonProperty("genesis-id")
	public String genesisId;

	@JsonProperty("transactions-root")
	public String transactionsRoot;

	@JsonProperty("round")
	public long round;

	@JsonProperty("upgrade-vote")
	public BlockUpgradeVote upgradeVote;
}
