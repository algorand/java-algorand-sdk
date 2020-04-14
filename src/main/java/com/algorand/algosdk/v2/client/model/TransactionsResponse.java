package com.algorand.algosdk.v2.client.model;

import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionsResponse extends PathResponse {

	/**
	 * Round at which the results were computed. 
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
	/**
	 * Check if has a value for currentRound 
	 */	@JsonIgnore
	public boolean hasCurrentRound(){
		return currentRoundIsSet;
	}

	/**
	 * Used for pagination, when making another request provide this token with the 
	 * next parameter. 
	 */
	private String nextToken;
	private boolean nextTokenIsSet;
	@JsonProperty("next-token")
	public void setNextToken(String nextToken){
		this.nextToken = nextToken;
		nextTokenIsSet = true;
	}
	@JsonProperty("next-token")
	public String getNextToken(){
		return nextTokenIsSet ? nextToken : null;
	}
	/**
	 * Check if has a value for nextToken 
	 */	@JsonIgnore
	public boolean hasNextToken(){
		return nextTokenIsSet;
	}

	private List<Transaction> transactions;
	private boolean transactionsIsSet;
	@JsonProperty("transactions")
	public void setTransactions(List<Transaction> transactions){
		this.transactions = transactions;
		transactionsIsSet = true;
	}
	@JsonProperty("transactions")
	public List<Transaction> getTransactions(){
		return transactionsIsSet ? transactions : null;
	}
	/**
	 * Check if has a value for transactions 
	 */	@JsonIgnore
	public boolean hasTransactions(){
		return transactionsIsSet;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		TransactionsResponse other = (TransactionsResponse) o;
		if (!Objects.deepEquals(this.currentRound, other.currentRound)) return false;
		if (!Objects.deepEquals(this.nextToken, other.nextToken)) return false;
		if (!Objects.deepEquals(this.transactions, other.transactions)) return false;

		return true;
	}
}
