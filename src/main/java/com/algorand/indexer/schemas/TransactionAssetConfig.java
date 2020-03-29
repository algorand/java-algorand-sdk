package com.algorand.indexer.schemas;

import com.algorand.indexer.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;

public class TransactionAssetConfig {

	public AssetParams params;
	public long assetId;
	
	public TransactionAssetConfig(JsonNode node) {
		this.params = new AssetParams(Utils.getNode("params", node));
		this.assetId = Utils.getLong("asset-id", node);
	}
}
