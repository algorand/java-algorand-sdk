package com.algorand.indexer.schemas;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	Describes an asset held by an account. Definition: data/basics/userBalance.go : 
	AssetHolding 
 */
public class AssetHolding {

	/*
		(a) number of units held. 
	 */
	@JsonProperty("amount")
	public long amount;

	/*
		Asset ID of the holding. 
	 */
	@JsonProperty("asset-id")
	public long assetId;

	/*
		Address that created this asset. This is the address where the parameters for 
		this asset can be found, and also the address where unwanted asset units can be 
		sent in the worst case. 
	 */
	@JsonProperty("creator")
	public String creator;

	/*
		(f) whether or not the holding is frozen. 
	 */
	@JsonProperty("is-frozen")
	public boolean isFrozen;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		AssetHolding other = (AssetHolding) o;
		if (!Objects.deepEquals(this.amount, other.amount)) return false;
		if (!Objects.deepEquals(this.assetId, other.assetId)) return false;
		if (!Objects.deepEquals(this.creator, other.creator)) return false;
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
