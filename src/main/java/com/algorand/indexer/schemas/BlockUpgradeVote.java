package com.algorand.indexer.schemas;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	Fields relating to voting for a protocol upgrade. 
 */
public class BlockUpgradeVote {

	/*
		(upgradeprop) Indicates a proposed upgrade. 
	 */
	@JsonProperty("upgrade-propose")
	public String upgradePropose;

	/*
		(upgradeyes) Indicates a yes vote for the current proposal. 
	 */
	@JsonProperty("upgrade-approve")
	public boolean upgradeApprove;

	/*
		(upgradedelay) Indicates the time between acceptance and execution. 
	 */
	@JsonProperty("upgrade-delay")
	public long upgradeDelay;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		BlockUpgradeVote other = (BlockUpgradeVote) o;
		if (!Objects.deepEquals(this.upgradePropose, other.upgradePropose)) return false;
		if (!Objects.deepEquals(this.upgradeApprove, other.upgradeApprove)) return false;
		if (!Objects.deepEquals(this.upgradeDelay, other.upgradeDelay)) return false;

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
