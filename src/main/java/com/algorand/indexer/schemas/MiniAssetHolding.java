package com.algorand.indexer.schemas;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	A simplified version of AssetHolding 
 */
public class MiniAssetHolding {

	@JsonProperty("address")
	public String address;

	@JsonProperty("amount")
	public long amount;

	@JsonProperty("is-frozen")
	public boolean isFrozen;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		MiniAssetHolding other = (MiniAssetHolding) o;
		if (!Objects.deepEquals(this.address, other.address)) return false;
		if (!Objects.deepEquals(this.amount, other.amount)) return false;
		if (!Objects.deepEquals(this.isFrozen, other.isFrozen)) return false;

		return true;
	}

	@Override
	public String toString() {
		ObjectMapper om = new ObjectMapper(); 
		String jsonStr;
		try {
			jsonStr = om.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
		}
		return jsonStr;
	}
}
