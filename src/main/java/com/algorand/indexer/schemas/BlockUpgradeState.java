package com.algorand.indexer.schemas;

import com.algorand.indexer.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;

public class BlockUpgradeState {
	public String nextProtocol;
	public long nextProtocolVoteBefore;
	public long nextProtocolApprovals;
	public long nextProtocolSwitchOn;
	public String currentProtocol;
	
	public BlockUpgradeState(String json) {
		this(Utils.getRoot(json));
	}
	public BlockUpgradeState(JsonNode node) {
		this.nextProtocol = Utils.getString("next-protocol", node);
		this.nextProtocolVoteBefore = Utils.getLong("next-protocol-vote-before", node);
		this.nextProtocolApprovals = Utils.getLong("next-protocol-approvals", node);
		this.nextProtocolSwitchOn = Utils.getLong("next-protocol-switch-on", node);
		this.currentProtocol = Utils.getString("current-protocol", node);
	}
}
