package com.algorand.indexer.schemas;

import com.algorand.indexer.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;

public class Transaction {
	public TransactionAssetTransfer assetTransferTransaction;
	public String note;
	public long lastValid;
	public String group;
	public String poolError;
	public String sender;
	public String id;
	public String genesisHash;
	public long fee;
	public long closeRewards;
	public long roundTime;
	public long firstValid;
	
	
	public Transaction(String json) {
		JsonNode node = Utils.getRoot(json);
		
		this.assetTransferTransaction = 
				new TransactionAssetTransfer(Utils.getNode("asset-transfer-transaction", node));
		this.note = Utils.getString("note", node);
		this.lastValid = Utils.getLong("last-valid", node);
		this.group = Utils.getString("group", node);
		this.poolError = Utils.getString("pool-error", node);
		this.sender = Utils.getString("sender", node);
		this.id = Utils.getString("id", node);
		this.genesisHash = Utils.getString("genesis-hash", node);
		this.fee = Utils.getLong("fee", node);
		this.closeRewards = Utils.getLong("close-rewards", node);
		this.roundTime = Utils.getLong("pathName", node);
		this.firstValid = Utils.getLong("first-valid", node);
	}
}
