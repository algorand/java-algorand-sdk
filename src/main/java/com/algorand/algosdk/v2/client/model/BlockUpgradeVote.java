package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Fields relating to voting for a protocol upgrade. 
 */
public class BlockUpgradeVote {

	/**
	 * (upgradeyes) Indicates a yes vote for the current proposal. 
	 */
	private boolean upgradeApprove;
	private boolean upgradeApproveIsSet;
	@JsonProperty("upgrade-approve")
	public void setUpgradeApprove(boolean upgradeApprove){
		this.upgradeApprove = upgradeApprove;
		upgradeApproveIsSet = true;
	}
	@JsonProperty("upgrade-approve")
	public Boolean getUpgradeApprove(){
		return upgradeApproveIsSet ? upgradeApprove : null;
	}
	/**
	 * Check if has a value for upgradeApprove 
	 */	@JsonIgnore
	public boolean hasUpgradeApprove(){
		return upgradeApproveIsSet;
	}

	/**
	 * (upgradedelay) Indicates the time between acceptance and execution. 
	 */
	private long upgradeDelay;
	private boolean upgradeDelayIsSet;
	@JsonProperty("upgrade-delay")
	public void setUpgradeDelay(long upgradeDelay){
		this.upgradeDelay = upgradeDelay;
		upgradeDelayIsSet = true;
	}
	@JsonProperty("upgrade-delay")
	public Long getUpgradeDelay(){
		return upgradeDelayIsSet ? upgradeDelay : null;
	}
	/**
	 * Check if has a value for upgradeDelay 
	 */	@JsonIgnore
	public boolean hasUpgradeDelay(){
		return upgradeDelayIsSet;
	}

	/**
	 * (upgradeprop) Indicates a proposed upgrade. 
	 */
	private String upgradePropose;
	private boolean upgradeProposeIsSet;
	@JsonProperty("upgrade-propose")
	public void setUpgradePropose(String upgradePropose){
		this.upgradePropose = upgradePropose;
		upgradeProposeIsSet = true;
	}
	@JsonProperty("upgrade-propose")
	public String getUpgradePropose(){
		return upgradeProposeIsSet ? upgradePropose : null;
	}
	/**
	 * Check if has a value for upgradePropose 
	 */	@JsonIgnore
	public boolean hasUpgradePropose(){
		return upgradeProposeIsSet;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		BlockUpgradeVote other = (BlockUpgradeVote) o;
		if (!Objects.deepEquals(this.upgradeApprove, other.upgradeApprove)) return false;
		if (!Objects.deepEquals(this.upgradeDelay, other.upgradeDelay)) return false;
		if (!Objects.deepEquals(this.upgradePropose, other.upgradePropose)) return false;

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
