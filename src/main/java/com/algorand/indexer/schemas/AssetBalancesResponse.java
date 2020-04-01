package com.algorand.indexer.schemas;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AssetBalancesResponse {

	@JsonProperty("balances")
	public List<MiniAssetHolding> balances;

	/*
		Round at which the results were computed. 
	 */
	@JsonProperty("current-round")
	public long currentRound;

	/*
		Used for pagination, when making another request provide this token with the 
		next parameter. 
	 */
	@JsonProperty("next-token")
	public String nextToken;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		AssetBalancesResponse other = (AssetBalancesResponse) o;
		if (!Objects.deepEquals(this.balances, other.balances)) return false;
		if (!Objects.deepEquals(this.currentRound, other.currentRound)) return false;
		if (!Objects.deepEquals(this.nextToken, other.nextToken)) return false;

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
