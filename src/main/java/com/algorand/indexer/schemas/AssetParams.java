package com.algorand.indexer.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AssetParams {

	@JsonProperty("reserve")
	public String reserve;

	@JsonProperty("creator")
	public String creator;

	@JsonProperty("unit-name")
	public String unitName;

	@JsonProperty("metadata-hash")
	public String metadataHash;

	@JsonProperty("total")
	public long total;

	@JsonProperty("url")
	public String url;

	@JsonProperty("freeze")
	public String freeze;

	@JsonProperty("decimals")
	public long decimals;

	@JsonProperty("manager")
	public String manager;

	@JsonProperty("clawback")
	public String clawback;

	@JsonProperty("default-frozen")
	public boolean defaultFrozen;

	@JsonProperty("name")
	public String name;
}
