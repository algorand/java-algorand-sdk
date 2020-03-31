package com.algorand.indexer.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AssetHolding {

	@JsonProperty("is-frozen")
	public boolean isFrozen;

	@JsonProperty("creator")
	public String creator;

	@JsonProperty("asset-id")
	public long assetId;

	@JsonProperty("amount")
	public long amount;
}
