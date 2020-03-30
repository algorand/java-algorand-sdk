package com.algorand.indexer.schemas;

import java.math.BigInteger;

import com.algorand.indexer.utils.Utils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class MiniAssetHolding {
	@JsonProperty("address")
	public String address;
	@JsonProperty("amount")
	public  BigInteger amount;
	@JsonProperty("is-frozen")
	public  boolean isFrozen;
	
	public MiniAssetHolding(String address, BigInteger amount, boolean isFrozen) {
		this.address = address;
		this.amount = amount;
		this.isFrozen = isFrozen;
	}
	
	public MiniAssetHolding(JsonNode node) {
		this.address = Utils.getString("address", node);
		this.amount = Utils.getBigInteger("amount", node);
		this.isFrozen = Utils.getBoolean("is-frozen", node);
	}
}
