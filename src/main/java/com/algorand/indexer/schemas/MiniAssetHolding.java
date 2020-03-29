package com.algorand.indexer.schemas;

import java.math.BigInteger;

import com.algorand.indexer.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;

public class MiniAssetHolding {
	public String address;
	public  BigInteger amount;
	public  boolean isFrozen;
	
	public MiniAssetHolding(JsonNode node) {
		this.address = Utils.getString("address", node);
		this.amount = Utils.getBigInteger("amount", node);
		this.isFrozen = Utils.getBoolean("is-frozen", node);
	}
}
