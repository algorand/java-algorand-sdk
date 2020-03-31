package com.algorand.indexer.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionAssetTransfer {

	@JsonProperty("amount")
	public long amount;

	@JsonProperty("asset-id")
	public long assetId;

	@JsonProperty("sender")
	public String sender;

	@JsonProperty("close-to")
	public String closeTo;

	@JsonProperty("receiver")
	public String receiver;
}
