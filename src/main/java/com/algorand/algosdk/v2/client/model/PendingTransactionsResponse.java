package com.algorand.algosdk.v2.client.model;

import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PendingTransactionsResponse extends PathResponse {

	/**
	 * An array of signed transaction objects. 
	 */
	private List<String> topTransactions;
	private boolean topTransactionsIsSet;
	@JsonProperty("top-transactions")
	public void setTopTransactions(List<String> topTransactions){
		this.topTransactions = topTransactions;
		topTransactionsIsSet = true;
	}
	@JsonProperty("top-transactions")
	public List<String> getTopTransactions(){
		return topTransactionsIsSet ? topTransactions : null;
	}
	/**
	 * Check if has a value for topTransactions 
	 */	@JsonIgnore
	public boolean hasTopTransactions(){
		return topTransactionsIsSet;
	}

	/**
	 * Total number of transactions in the pool. 
	 */
	private long totalTransactions;
	private boolean totalTransactionsIsSet;
	@JsonProperty("total-transactions")
	public void setTotalTransactions(long totalTransactions){
		this.totalTransactions = totalTransactions;
		totalTransactionsIsSet = true;
	}
	@JsonProperty("total-transactions")
	public Long getTotalTransactions(){
		return totalTransactionsIsSet ? totalTransactions : null;
	}
	/**
	 * Check if has a value for totalTransactions 
	 */	@JsonIgnore
	public boolean hasTotalTransactions(){
		return totalTransactionsIsSet;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		PendingTransactionsResponse other = (PendingTransactionsResponse) o;
		if (!Objects.deepEquals(this.topTransactions, other.topTransactions)) return false;
		if (!Objects.deepEquals(this.totalTransactions, other.totalTransactions)) return false;

		return true;
	}
}
