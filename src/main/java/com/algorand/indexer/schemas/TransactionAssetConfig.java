package com.algorand.indexer.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionAssetConfig {

	@JsonProperty("params")
	public AssetParams params;

	@JsonProperty("asset-id")
	public long assetId;
}
