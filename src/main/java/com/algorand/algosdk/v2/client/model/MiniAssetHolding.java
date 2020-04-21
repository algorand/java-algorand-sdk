package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A simplified version of AssetHolding 
 */
public class MiniAssetHolding extends PathResponse {

	@JsonProperty("address")
	public String address;

	@JsonProperty("amount")
	public Long amount;

	@JsonProperty("is-frozen")
	public Boolean isFrozen;

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
}
