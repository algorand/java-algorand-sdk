package com.algorand.indexer.schemas;

import com.algorand.indexer.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class Account {

	public long rewardBase;
	public long rewards;
	public String address;
	public String status;
	public AccountParticipation participation;
	public Asset [] createdAssets;
	public String type;
	public long pendingRewards;
	public long amountWithoutPendingRewards;
	public long amount;
	public long round;
	public AssetHolding[] assets;
	
	public Account(String json) {
		JsonNode node = Utils.getRoot(json);
		this.rewardBase = Utils.getLong("reward-base", node);
		this.rewards = Utils.getLong("rewards", node);
		this.address = Utils.getString("address", node);
		this.status = Utils.getString("status", node);
		this.participation = new AccountParticipation(
				Utils.getNode("participation", node));
		ArrayNode an = Utils.getArrayNode("created-assets", node);
		this.createdAssets = new Asset[an.size()];
		for (int i = 0; i < an.size(); i++) {
			this.createdAssets[i] = new Asset(an.get(i));
		}
		this.type = Utils.getString("type", node);
		this.pendingRewards = Utils.getLong("pending-rewards", node);
		this.amountWithoutPendingRewards = Utils.getLong("amount-without-pending-rewards", node);
		this.amount = Utils.getLong("amount", node);
		this.round = Utils.getLong("round", node);
		an = Utils.getArrayNode("assets", node);
		this.assets = new AssetHolding[an.size()];
		for (int i = 0; i < an.size(); i++) {
			this.assets[i] = new AssetHolding(an.get(i));
		}
		
	}
}
