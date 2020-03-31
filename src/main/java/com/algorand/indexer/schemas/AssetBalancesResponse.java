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
}
