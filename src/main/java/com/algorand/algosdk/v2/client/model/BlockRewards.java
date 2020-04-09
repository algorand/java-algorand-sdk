package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Fields relating to rewards, 
 */
public class BlockRewards {

	/**
	 * (fees) accepts transaction fees, it can only spend to the incentive pool. 
	 */
	private String feeSink;
	private boolean feeSinkIsSet;
	@JsonProperty("fee-sink")
	public void setFeeSink(String feeSink){
		this.feeSink = feeSink;
		feeSinkIsSet = true;
	}
	@JsonProperty("fee-sink")
	public String getFeeSink(){
		return feeSinkIsSet ? feeSink : null;
	}
	/**
	 * Check if has a value for feeSink 
	 */	@JsonIgnore
	public boolean hasFeeSink(){
		return feeSinkIsSet;
	}

	/**
	 * (rwcalr) number of leftover MicroAlgos after the distribution of rewards-rate 
	 * MicroAlgos for every reward unit in the next round. 
	 */
	private long rewardsCalculationRound;
	private boolean rewardsCalculationRoundIsSet;
	@JsonProperty("rewards-calculation-round")
	public void setRewardsCalculationRound(long rewardsCalculationRound){
		this.rewardsCalculationRound = rewardsCalculationRound;
		rewardsCalculationRoundIsSet = true;
	}
	@JsonProperty("rewards-calculation-round")
	public Long getRewardsCalculationRound(){
		return rewardsCalculationRoundIsSet ? rewardsCalculationRound : null;
	}
	/**
	 * Check if has a value for rewardsCalculationRound 
	 */	@JsonIgnore
	public boolean hasRewardsCalculationRound(){
		return rewardsCalculationRoundIsSet;
	}

	/**
	 * (earn) How many rewards, in MicroAlgos, have been distributed to each RewardUnit 
	 * of MicroAlgos since genesis. 
	 */
	private long rewardsLevel;
	private boolean rewardsLevelIsSet;
	@JsonProperty("rewards-level")
	public void setRewardsLevel(long rewardsLevel){
		this.rewardsLevel = rewardsLevel;
		rewardsLevelIsSet = true;
	}
	@JsonProperty("rewards-level")
	public Long getRewardsLevel(){
		return rewardsLevelIsSet ? rewardsLevel : null;
	}
	/**
	 * Check if has a value for rewardsLevel 
	 */	@JsonIgnore
	public boolean hasRewardsLevel(){
		return rewardsLevelIsSet;
	}

	/**
	 * (rwd) accepts periodic injections from the fee-sink and continually 
	 * redistributes them as rewards. 
	 */
	private String rewardsPool;
	private boolean rewardsPoolIsSet;
	@JsonProperty("rewards-pool")
	public void setRewardsPool(String rewardsPool){
		this.rewardsPool = rewardsPool;
		rewardsPoolIsSet = true;
	}
	@JsonProperty("rewards-pool")
	public String getRewardsPool(){
		return rewardsPoolIsSet ? rewardsPool : null;
	}
	/**
	 * Check if has a value for rewardsPool 
	 */	@JsonIgnore
	public boolean hasRewardsPool(){
		return rewardsPoolIsSet;
	}

	/**
	 * (rate) Number of new MicroAlgos added to the participation stake from rewards at 
	 * the next round. 
	 */
	private long rewardsRate;
	private boolean rewardsRateIsSet;
	@JsonProperty("rewards-rate")
	public void setRewardsRate(long rewardsRate){
		this.rewardsRate = rewardsRate;
		rewardsRateIsSet = true;
	}
	@JsonProperty("rewards-rate")
	public Long getRewardsRate(){
		return rewardsRateIsSet ? rewardsRate : null;
	}
	/**
	 * Check if has a value for rewardsRate 
	 */	@JsonIgnore
	public boolean hasRewardsRate(){
		return rewardsRateIsSet;
	}

	/**
	 * (frac) Number of leftover MicroAlgos after the distribution of 
	 * RewardsRate/rewardUnits MicroAlgos for every reward unit in the next round. 
	 */
	private long rewardsResidue;
	private boolean rewardsResidueIsSet;
	@JsonProperty("rewards-residue")
	public void setRewardsResidue(long rewardsResidue){
		this.rewardsResidue = rewardsResidue;
		rewardsResidueIsSet = true;
	}
	@JsonProperty("rewards-residue")
	public Long getRewardsResidue(){
		return rewardsResidueIsSet ? rewardsResidue : null;
	}
	/**
	 * Check if has a value for rewardsResidue 
	 */	@JsonIgnore
	public boolean hasRewardsResidue(){
		return rewardsResidueIsSet;
	}

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

	@Override
	public String toString() {
		ObjectMapper om = new ObjectMapper(); 
		String jsonStr;
		try {
			jsonStr = om.setSerializationInclusion(Include.NON_NULL).writeValueAsString(this);

		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
		}
		return jsonStr;
	}
}
