package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SupplyResponse {

	/*
		Round 
	 */
	@JsonProperty("current_round")
	public long current_round;

	/*
		OnlineMoney 
	 */
	@JsonProperty("online-money")
	public long onlineMoney;

	/*
		TotalMoney 
	 */
	@JsonProperty("total-money")
	public long totalMoney;

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
			jsonStr = om.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
		}
		return jsonStr;
	}
}
