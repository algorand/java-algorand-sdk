package com.algorand.indexer.schemas;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Account {
	@JsonProperty("reward-base")
	public long rewardBase;

	@JsonProperty("rewards")
	public long rewards;

	@JsonProperty("address")
	public String address;

	@JsonProperty("status")
	public String status;

	@JsonProperty("participation")
	public AccountParticipation participation;

	@JsonProperty("created-assets")
	public List<Asset> createdAssets;

	@JsonProperty("type")
	public String type;

	@JsonProperty("pending-rewards")
	public long pendingRewards;

	@JsonProperty("amount-without-pending-rewards")
	public long amountWithoutPendingRewards;

	@JsonProperty("amount")
	public long amount;

	@JsonProperty("round")
	public long round;

	@JsonProperty("assets")
	public List<AssetHolding> assets;
}
