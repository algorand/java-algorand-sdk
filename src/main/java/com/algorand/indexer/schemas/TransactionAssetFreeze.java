package com.algorand.indexer.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionAssetFreeze {

	@JsonProperty("new-freeze-status")
	public boolean newFreezeStatus;

	@JsonProperty("asset-id")
	public long assetId;

	@JsonProperty("address")
	public String address;
}
