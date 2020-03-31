package com.algorand.indexer.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Asset {

	@JsonProperty("params")
	public AssetParams params;

	@JsonProperty("index")
	public long index;
}
