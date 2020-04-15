package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SupplyResponse extends PathResponse {

	/**
	 * Round 
	 */	@JsonProperty("current_round")
	public Long current_round;

	/**
	 * OnlineMoney 
	 */	@JsonProperty("online-money")
	public Long onlineMoney;

	/**
	 * TotalMoney 
	 */	@JsonProperty("total-money")
	public Long totalMoney;

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
}
