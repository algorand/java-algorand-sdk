package com.algorand.indexer.schemas;

import com.algorand.indexer.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;

public class Asset {
	public AssetParams params;
	public long index;
	
	public Asset(String json) {
		this(Utils.getRoot(json));
	}
	public Asset(JsonNode node) {
		this.params = new AssetParams(Utils.getNode("params", node));
		this.index = Utils.getLong("index", node);
	}
}
