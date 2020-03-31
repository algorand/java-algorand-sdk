package com.algorand.indexer.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BlockUpgradeState {

	@JsonProperty("next-protocol")
	public String nextProtocol;

	@JsonProperty("next-protocol-vote-before")
	public long nextProtocolVoteBefore;

	@JsonProperty("next-protocol-approvals")
	public long nextProtocolApprovals;

	@JsonProperty("next-protocol-switch-on")
	public long nextProtocolSwitchOn;

	@JsonProperty("current-protocol")
	public String currentProtocol;
}
