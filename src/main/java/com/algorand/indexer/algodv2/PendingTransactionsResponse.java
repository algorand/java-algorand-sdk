package com.algorand.indexer.algodv2;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PendingTransactionsResponse {

	/*
		An array of transactions encoded as either a JSON string or a Base64 encoded 
		message pack object. 
	 */
	@JsonProperty("top-transactions")
	public List<String> topTransactions;

	/*
		Total number of transactions in the pool. 
	 */
	@JsonProperty("total-transactions")
	public long totalTransactions;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		PendingTransactionsResponse other = (PendingTransactionsResponse) o;
		if (!Objects.deepEquals(this.topTransactions, other.topTransactions)) return false;
		if (!Objects.deepEquals(this.totalTransactions, other.totalTransactions)) return false;

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
