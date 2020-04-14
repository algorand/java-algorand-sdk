package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Fields for an asset freeze transaction. Definition: data/transactions/asset.go : 
 * AssetFreezeTxnFields 
 */
public class TransactionAssetFreeze extends PathResponse {

	/**
	 * (fadd) Address of the account whose asset is being frozen or thawed. 
	 */
	private String address;
	private boolean addressIsSet;
	@JsonProperty("address")
	public void setAddress(String address){
		this.address = address;
		addressIsSet = true;
	}
	@JsonProperty("address")
	public String getAddress(){
		return addressIsSet ? address : null;
	}
	/**
	 * Check if has a value for address 
	 */	@JsonIgnore
	public boolean hasAddress(){
		return addressIsSet;
	}

	/**
	 * (faid) ID of the asset being frozen or thawed. 
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
	 * (afrz) The new freeze status. 
	 */
	private boolean newFreezeStatus;
	private boolean newFreezeStatusIsSet;
	@JsonProperty("new-freeze-status")
	public void setNewFreezeStatus(boolean newFreezeStatus){
		this.newFreezeStatus = newFreezeStatus;
		newFreezeStatusIsSet = true;
	}
	@JsonProperty("new-freeze-status")
	public Boolean getNewFreezeStatus(){
		return newFreezeStatusIsSet ? newFreezeStatus : null;
	}
	/**
	 * Check if has a value for newFreezeStatus 
	 */	@JsonIgnore
	public boolean hasNewFreezeStatus(){
		return newFreezeStatusIsSet;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		TransactionAssetFreeze other = (TransactionAssetFreeze) o;
		if (!Objects.deepEquals(this.address, other.address)) return false;
		if (!Objects.deepEquals(this.assetId, other.assetId)) return false;
		if (!Objects.deepEquals(this.newFreezeStatus, other.newFreezeStatus)) return false;

		return true;
	}
}
