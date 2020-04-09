package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * A simplified version of AssetHolding 
 */
public class MiniAssetHolding {

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

		MiniAssetHolding other = (MiniAssetHolding) o;
		if (!Objects.deepEquals(this.address, other.address)) return false;
		if (!Objects.deepEquals(this.amount, other.amount)) return false;
		if (!Objects.deepEquals(this.isFrozen, other.isFrozen)) return false;

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
