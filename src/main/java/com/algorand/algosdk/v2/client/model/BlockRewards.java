package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Fields relating to rewards, 
 */
public class BlockRewards extends PathResponse {

	/**
	 * (fees) accepts transaction fees, it can only spend to the incentive pool. 
	 */
	@JsonProperty("fee-sink")
	public String feeSink;

	/**
	 * (rwcalr) number of leftover MicroAlgos after the distribution of rewards-rate 
	 * MicroAlgos for every reward unit in the next round. 
	 */
	@JsonProperty("rewards-calculation-round")
	public Long rewardsCalculationRound;

	/**
	 * (earn) How many rewards, in MicroAlgos, have been distributed to each RewardUnit 
	 * of MicroAlgos since genesis. 
	 */
	@JsonProperty("rewards-level")
	public Long rewardsLevel;

	/**
	 * (rwd) accepts periodic injections from the fee-sink and continually 
	 * redistributes them as rewards. 
	 */
	@JsonProperty("rewards-pool")
	public String rewardsPool;

	/**
	 * (rate) Number of new MicroAlgos added to the participation stake from rewards at 
	 * the next round. 
	 */
	@JsonProperty("rewards-rate")
	public Long rewardsRate;

	/**
	 * (frac) Number of leftover MicroAlgos after the distribution of 
	 * RewardsRate/rewardUnits MicroAlgos for every reward unit in the next round. 
	 */
	@JsonProperty("rewards-residue")
	public Long rewardsResidue;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		BlockRewards other = (BlockRewards) o;
		if (!Objects.deepEquals(this.feeSink, other.feeSink)) return false;
		if (!Objects.deepEquals(this.rewardsCalculationRound, other.rewardsCalculationRound)) return false;
		if (!Objects.deepEquals(this.rewardsLevel, other.rewardsLevel)) return false;
		if (!Objects.deepEquals(this.rewardsPool, other.rewardsPool)) return false;
		if (!Objects.deepEquals(this.rewardsRate, other.rewardsRate)) return false;
		if (!Objects.deepEquals(this.rewardsResidue, other.rewardsResidue)) return false;

		return true;
	}
}
