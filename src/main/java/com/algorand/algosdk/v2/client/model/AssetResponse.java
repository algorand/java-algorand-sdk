package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AssetResponse {

	@JsonProperty("asset")
	public Asset asset;

	/*
		Round at which the results were computed. 
	 */
	@JsonProperty("current-round")
	public long currentRound;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		AssetResponse other = (AssetResponse) o;
		if (!Objects.deepEquals(this.asset, other.asset)) return false;
		if (!Objects.deepEquals(this.currentRound, other.currentRound)) return false;

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
