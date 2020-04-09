package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SupplyResponse {

	/*
		Round 
	 */
	private long current_round;
	private boolean current_roundIsSet;
	@JsonProperty("current_round")
	public void setCurrent_round(long current_round){
		this.current_round = current_round;
		current_roundIsSet = true;
	}
	@JsonProperty("current_round")
	public Long getCurrent_round(){
		return current_roundIsSet ? current_round : null;
	}
	/*
		Check if has a value for current_round 
	 */	@JsonIgnore
	public boolean hasCurrent_round(){
		return current_roundIsSet;
	}

	/*
		OnlineMoney 
	 */
	private long onlineMoney;
	private boolean onlineMoneyIsSet;
	@JsonProperty("online-money")
	public void setOnlineMoney(long onlineMoney){
		this.onlineMoney = onlineMoney;
		onlineMoneyIsSet = true;
	}
	@JsonProperty("online-money")
	public Long getOnlineMoney(){
		return onlineMoneyIsSet ? onlineMoney : null;
	}
	/*
		Check if has a value for onlineMoney 
	 */	@JsonIgnore
	public boolean hasOnlineMoney(){
		return onlineMoneyIsSet;
	}

	/*
		TotalMoney 
	 */
	private long totalMoney;
	private boolean totalMoneyIsSet;
	@JsonProperty("total-money")
	public void setTotalMoney(long totalMoney){
		this.totalMoney = totalMoney;
		totalMoneyIsSet = true;
	}
	@JsonProperty("total-money")
	public Long getTotalMoney(){
		return totalMoneyIsSet ? totalMoney : null;
	}
	/*
		Check if has a value for totalMoney 
	 */	@JsonIgnore
	public boolean hasTotalMoney(){
		return totalMoneyIsSet;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		SupplyResponse other = (SupplyResponse) o;
		if (!Objects.deepEquals(this.current_round, other.current_round)) return false;
		if (!Objects.deepEquals(this.onlineMoney, other.onlineMoney)) return false;
		if (!Objects.deepEquals(this.totalMoney, other.totalMoney)) return false;

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
