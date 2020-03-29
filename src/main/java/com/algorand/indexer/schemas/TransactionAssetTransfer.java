package com.algorand.indexer.schemas;

import com.algorand.indexer.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;

public class TransactionAssetTransfer {

	public long amount;
	public long assetId;
	public String sender;
	public String closeTo;
	public String receiver;
	
	public TransactionAssetTransfer(JsonNode node) {
		this.amount = Utils.getLong("amount", node);
		this.assetId = Utils.getLong("asset-id", node);
		this.sender = Utils.getString("sender", node);
		this.closeTo = Utils.getString("close-to", node);
		this.receiver = Utils.getString("receiver", node);
	}
}
