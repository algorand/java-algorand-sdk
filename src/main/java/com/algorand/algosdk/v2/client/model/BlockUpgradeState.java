package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	Fields relating to a protocol upgrade. 
 */
public class BlockUpgradeState {

	/*
		(proto) The current protocol version. 
	 */
	@JsonProperty("current-protocol")
	public String currentProtocol;

	/*
		(nextproto) The next proposed protocol version. 
	 */
	@JsonProperty("next-protocol")
	public String nextProtocol;

	/*
		(nextyes) Number of blocks which approved the protocol upgrade. 
	 */
	@JsonProperty("next-protocol-approvals")
	public long nextProtocolApprovals;

	/*
		(nextswitch) Round on which the protocol upgrade will take effect. 
	 */
	@JsonProperty("next-protocol-switch-on")
	public long nextProtocolSwitchOn;

	/*
		(nextbefore) Deadline round for this protocol upgrade (No votes will be consider 
		after this round). 
	 */
	@JsonProperty("next-protocol-vote-before")
	public long nextProtocolVoteBefore;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		BlockUpgradeState other = (BlockUpgradeState) o;
		if (!Objects.deepEquals(this.currentProtocol, other.currentProtocol)) return false;
		if (!Objects.deepEquals(this.nextProtocol, other.nextProtocol)) return false;
		if (!Objects.deepEquals(this.nextProtocolApprovals, other.nextProtocolApprovals)) return false;
		if (!Objects.deepEquals(this.nextProtocolSwitchOn, other.nextProtocolSwitchOn)) return false;
		if (!Objects.deepEquals(this.nextProtocolVoteBefore, other.nextProtocolVoteBefore)) return false;

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
