package com.algorand.indexer.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BlockRewards {

	@JsonProperty("fee-sink")
	public String feeSink;

	@JsonProperty("rewards-rate")
	public long rewardsRate;

	@JsonProperty("rewards-pool")
	public String rewardsPool;

	@JsonProperty("rewards-level")
	public long rewardsLevel;

	@JsonProperty("rewards-residue")
	public long rewardsResidue;

	@JsonProperty("rewards-calculation-round")
	public long rewardsCalculationRound;
}
