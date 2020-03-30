package com.algorand.indexer.schemas;

import com.algorand.indexer.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;

public class TransactionAssetFreeze {
	public boolean newFreezeStatus;
	public long assetId;
	public String address;
	
	
	public TransactionAssetFreeze(JsonNode node) {
		this.newFreezeStatus = Utils.getBoolean("new-freeze-status", node);
		this.assetId = Utils.getLong("asset-id", node);
		this.address = Utils.getString("address", node);
	}	
		
	public TransactionAssetFreeze(String json) {
		this(Utils.getRoot(json));
	}
	

}
