package com.algorand.indexer.schemas;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AssetBalancesResponse {

	@JsonProperty("next-token")
	public String nextToken;
	
	@JsonProperty("current-round")
	public long currentRound;
	
	@JsonProperty("balances")
	public List<MiniAssetHolding> balances;
	
	public AssetBalancesResponse() {}
	public AssetBalancesResponse(String nextToken, long currentRound, List<MiniAssetHolding> balances) {
		this.nextToken = nextToken;
		this.currentRound = currentRound;
		this.balances = balances;
	}
}
