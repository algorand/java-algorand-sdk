package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Describes an asset held by an account. 
 * Definition: 
 * data/basics/userBalance.go : AssetHolding 
 */
public class AssetHolding extends PathResponse {

	/**
	 * (a) number of units held. 
	 */
	@JsonProperty("amount")
	public java.math.BigInteger amount;

	/**
	 * Asset ID of the holding. 
	 */
	@JsonProperty("asset-id")
	public Long assetId;

	/**
	 * Address that created this asset. This is the address where the parameters for 
	 * this asset can be found, and also the address where unwanted asset units can be 
	 * sent in the worst case. 
	 */
	@JsonProperty("creator")
	public String creator;

	/**
	 * (f) whether or not the holding is frozen. 
	 */
	@JsonProperty("is-frozen")
	public Boolean isFrozen;

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
}
