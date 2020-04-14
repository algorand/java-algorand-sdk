package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Fields relating to a protocol upgrade. 
 */
public class BlockUpgradeState extends PathResponse {

	/**
	 * (proto) The current protocol version. 
	 */
	private String currentProtocol;
	private boolean currentProtocolIsSet;
	@JsonProperty("current-protocol")
	public void setCurrentProtocol(String currentProtocol){
		this.currentProtocol = currentProtocol;
		currentProtocolIsSet = true;
	}
	@JsonProperty("current-protocol")
	public String getCurrentProtocol(){
		return currentProtocolIsSet ? currentProtocol : null;
	}
	/**
	 * Check if has a value for currentProtocol 
	 */	@JsonIgnore
	public boolean hasCurrentProtocol(){
		return currentProtocolIsSet;
	}

	/**
	 * (nextproto) The next proposed protocol version. 
	 */
	private String nextProtocol;
	private boolean nextProtocolIsSet;
	@JsonProperty("next-protocol")
	public void setNextProtocol(String nextProtocol){
		this.nextProtocol = nextProtocol;
		nextProtocolIsSet = true;
	}
	@JsonProperty("next-protocol")
	public String getNextProtocol(){
		return nextProtocolIsSet ? nextProtocol : null;
	}
	/**
	 * Check if has a value for nextProtocol 
	 */	@JsonIgnore
	public boolean hasNextProtocol(){
		return nextProtocolIsSet;
	}

	/**
	 * (nextyes) Number of blocks which approved the protocol upgrade. 
	 */
	private long nextProtocolApprovals;
	private boolean nextProtocolApprovalsIsSet;
	@JsonProperty("next-protocol-approvals")
	public void setNextProtocolApprovals(long nextProtocolApprovals){
		this.nextProtocolApprovals = nextProtocolApprovals;
		nextProtocolApprovalsIsSet = true;
	}
	@JsonProperty("next-protocol-approvals")
	public Long getNextProtocolApprovals(){
		return nextProtocolApprovalsIsSet ? nextProtocolApprovals : null;
	}
	/**
	 * Check if has a value for nextProtocolApprovals 
	 */	@JsonIgnore
	public boolean hasNextProtocolApprovals(){
		return nextProtocolApprovalsIsSet;
	}

	/**
	 * (nextswitch) Round on which the protocol upgrade will take effect. 
	 */
	private long nextProtocolSwitchOn;
	private boolean nextProtocolSwitchOnIsSet;
	@JsonProperty("next-protocol-switch-on")
	public void setNextProtocolSwitchOn(long nextProtocolSwitchOn){
		this.nextProtocolSwitchOn = nextProtocolSwitchOn;
		nextProtocolSwitchOnIsSet = true;
	}
	@JsonProperty("next-protocol-switch-on")
	public Long getNextProtocolSwitchOn(){
		return nextProtocolSwitchOnIsSet ? nextProtocolSwitchOn : null;
	}
	/**
	 * Check if has a value for nextProtocolSwitchOn 
	 */	@JsonIgnore
	public boolean hasNextProtocolSwitchOn(){
		return nextProtocolSwitchOnIsSet;
	}

	/**
	 * (nextbefore) Deadline round for this protocol upgrade (No votes will be consider 
	 * after this round). 
	 */
	private long nextProtocolVoteBefore;
	private boolean nextProtocolVoteBeforeIsSet;
	@JsonProperty("next-protocol-vote-before")
	public void setNextProtocolVoteBefore(long nextProtocolVoteBefore){
		this.nextProtocolVoteBefore = nextProtocolVoteBefore;
		nextProtocolVoteBeforeIsSet = true;
	}
	@JsonProperty("next-protocol-vote-before")
	public Long getNextProtocolVoteBefore(){
		return nextProtocolVoteBeforeIsSet ? nextProtocolVoteBefore : null;
	}
	/**
	 * Check if has a value for nextProtocolVoteBefore 
	 */	@JsonIgnore
	public boolean hasNextProtocolVoteBefore(){
		return nextProtocolVoteBeforeIsSet;
	}

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
}
