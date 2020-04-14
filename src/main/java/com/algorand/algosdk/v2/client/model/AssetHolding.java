package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Describes an asset held by an account. Definition: data/basics/userBalance.go : 
 * AssetHolding 
 */
public class AssetHolding extends PathResponse {

	/**
	 * (a) number of units held. 
	 */
	private long amount;
	private boolean amountIsSet;
	@JsonProperty("amount")
	public void setAmount(long amount){
		this.amount = amount;
		amountIsSet = true;
	}
	@JsonProperty("amount")
	public Long getAmount(){
		return amountIsSet ? amount : null;
	}
	/**
	 * Check if has a value for amount 
	 */	@JsonIgnore
	public boolean hasAmount(){
		return amountIsSet;
	}

	/**
	 * Asset ID of the holding. 
	 */
	private long assetId;
	private boolean assetIdIsSet;
	@JsonProperty("asset-id")
	public void setAssetId(long assetId){
		this.assetId = assetId;
		assetIdIsSet = true;
	}
	@JsonProperty("asset-id")
	public Long getAssetId(){
		return assetIdIsSet ? assetId : null;
	}
	/**
	 * Check if has a value for assetId 
	 */	@JsonIgnore
	public boolean hasAssetId(){
		return assetIdIsSet;
	}

	/**
	 * Address that created this asset. This is the address where the parameters for 
	 * this asset can be found, and also the address where unwanted asset units can be 
	 * sent in the worst case. 
	 */
	private String creator;
	private boolean creatorIsSet;
	@JsonProperty("creator")
	public void setCreator(String creator){
		this.creator = creator;
		creatorIsSet = true;
	}
	@JsonProperty("creator")
	public String getCreator(){
		return creatorIsSet ? creator : null;
	}
	/**
	 * Check if has a value for creator 
	 */	@JsonIgnore
	public boolean hasCreator(){
		return creatorIsSet;
	}

	/**
	 * (f) whether or not the holding is frozen. 
	 */
	private boolean isFrozen;
	private boolean isFrozenIsSet;
	@JsonProperty("is-frozen")
	public void setIsFrozen(boolean isFrozen){
		this.isFrozen = isFrozen;
		isFrozenIsSet = true;
	}
	@JsonProperty("is-frozen")
	public Boolean getIsFrozen(){
		return isFrozenIsSet ? isFrozen : null;
	}
	/**
	 * Check if has a value for isFrozen 
	 */	@JsonIgnore
	public boolean hasIsFrozen(){
		return isFrozenIsSet;
	}

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
