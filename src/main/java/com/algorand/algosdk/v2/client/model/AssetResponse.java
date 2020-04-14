package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AssetResponse extends PathResponse {

	private Asset asset;
	private boolean assetIsSet;
	@JsonProperty("asset")
	public void setAsset(Asset asset){
		this.asset = asset;
		assetIsSet = true;
	}
	@JsonProperty("asset")
	public Asset getAsset(){
		return assetIsSet ? asset : null;
	}
	/**
	 * Check if has a value for asset 
	 */	@JsonIgnore
	public boolean hasAsset(){
		return assetIsSet;
	}

	/**
	 * Round at which the results were computed. 
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
	/**
	 * Check if has a value for currentRound 
	 */	@JsonIgnore
	public boolean hasCurrentRound(){
		return currentRoundIsSet;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		AssetResponse other = (AssetResponse) o;
		if (!Objects.deepEquals(this.asset, other.asset)) return false;
		if (!Objects.deepEquals(this.currentRound, other.currentRound)) return false;

		return true;
	}
}
