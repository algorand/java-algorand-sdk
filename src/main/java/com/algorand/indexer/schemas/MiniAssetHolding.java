package com.algorand.indexer.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MiniAssetHolding {

	@JsonProperty("is-frozen")
	public boolean isFrozen;

	@JsonProperty("address")
	public String address;

	@JsonProperty("amount")
	public long amount;
}
