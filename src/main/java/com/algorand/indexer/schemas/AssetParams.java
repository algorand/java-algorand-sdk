package com.algorand.indexer.schemas;

import com.algorand.indexer.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;

public class AssetParams {
	
	public String reserve;
	public String creator;
	public String unitName;
	public String metadataHash;
	public long total;
	public String url;
	public String freeze;
	public short decimals;
	public String manager;
	public String clawback;
	public boolean defaultFrozen;
	public String name;
	
	public AssetParams(String json) {
		this(Utils.getRoot(json));
	}
	public AssetParams(JsonNode node) {
		this.reserve = Utils.getString("reserve", node);
		this.creator = Utils.getString("creator", node);
		this.unitName = Utils.getString("unit-name", node);
		this.metadataHash = Utils.getBase64String("metadata-hash", node);
		this.total = Utils.getLong("total", node);
		this.url = Utils.getString("url", node);
		this.freeze = Utils.getString("freeze", node);
		this.decimals = (short)Utils.getLong("decimals", node);
		this.manager = Utils.getString("manager", node);
		this.clawback = Utils.getString("clawback", node);
		this.defaultFrozen = Utils.getBoolean("default-frozen", node);
		this.name = Utils.getString("name", node);
	}
}
