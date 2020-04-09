package com.algorand.algosdk.v2.client.model;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AssetsResponse {

	private List<Asset> assets;
	private boolean assetsIsSet;
	@JsonProperty("assets")
	public void setAssets(List<Asset> assets){
		this.assets = assets;
		assetsIsSet = true;
	}
	@JsonProperty("assets")
	public List<Asset> getAssets(){
		return assetsIsSet ? assets : null;
	}
	/*
		Check if has a value for assets 
	 */	@JsonIgnore
	public boolean hasAssets(){
		return assetsIsSet;
	}

	/*
		Round at which the results were computed. 
	 */
	private long currentRound;
	private boolean currentRoundIsSet;
	@JsonProperty("current-round")
	public void setCurrentRound(long currentRound){
		this.currentRound = currentRound;
		currentRoundIsSet = true;
	}
	@JsonProperty("current-round")
	public Long getCurrentRound(){
		return currentRoundIsSet ? currentRound : null;
	}
	/*
		Check if has a value for currentRound 
	 */	@JsonIgnore
	public boolean hasCurrentRound(){
		return currentRoundIsSet;
	}

	/*
		Used for pagination, when making another request provide this token with the 
		next parameter. 
	 */
	private String nextToken;
	private boolean nextTokenIsSet;
	@JsonProperty("next-token")
	public void setNextToken(String nextToken){
		this.nextToken = nextToken;
		nextTokenIsSet = true;
	}
	@JsonProperty("next-token")
	public String getNextToken(){
		return nextTokenIsSet ? nextToken : null;
	}
	/*
		Check if has a value for nextToken 
	 */	@JsonIgnore
	public boolean hasNextToken(){
		return nextTokenIsSet;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		AssetsResponse other = (AssetsResponse) o;
		if (!Objects.deepEquals(this.assets, other.assets)) return false;
		if (!Objects.deepEquals(this.currentRound, other.currentRound)) return false;
		if (!Objects.deepEquals(this.nextToken, other.nextToken)) return false;

		return true;
	}

	@Override
	public String toString() {
		ObjectMapper om = new ObjectMapper(); 
		String jsonStr;
		try {
			jsonStr = om.setSerializationInclusion(Include.NON_NULL).writeValueAsString(this);

		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
		}
		return jsonStr;
	}
}
