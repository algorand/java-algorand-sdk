package com.algorand.indexer.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BlockUpgradeVote {

	@JsonProperty("upgrade-propose")
	public String upgradePropose;

	@JsonProperty("upgrade-approve")
	public boolean upgradeApprove;

	@JsonProperty("upgrade-delay")
	public long upgradeDelay;
}
