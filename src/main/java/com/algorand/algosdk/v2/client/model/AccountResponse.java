package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AccountResponse {

	private Account account;
	private boolean accountIsSet;
	@JsonProperty("account")
	public void setAccount(Account account){
		this.account = account;
		accountIsSet = true;
	}
	@JsonProperty("account")
	public Account getAccount(){
		return accountIsSet ? account : null;
	}
	/*
		Check if has a value for account 
	 */	@JsonIgnore
	public boolean hasAccount(){
		return accountIsSet;
	}

	/*
		Round at which the results were computed. 
	 */
	private long currentRound;
	private boolean currentRoundIsSet;
	@JsonProperty("current-round")
	public void setCurrentRound(long currentRound){
		this.currentRound = currentRound;
		currentRoundIsSet = true;
	}
	@JsonProperty("current-round")
	public Long getCurrentRound(){
		return currentRoundIsSet ? currentRound : null;
	}
	/*
		Check if has a value for currentRound 
	 */	@JsonIgnore
	public boolean hasCurrentRound(){
		return currentRoundIsSet;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		AccountResponse other = (AccountResponse) o;
		if (!Objects.deepEquals(this.account, other.account)) return false;
		if (!Objects.deepEquals(this.currentRound, other.currentRound)) return false;

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
