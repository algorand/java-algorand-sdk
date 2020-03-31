package com.algorand.indexer.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionPayment {
	
	@JsonProperty("close-remainder-to")
	public String closeRemainderTo; 
	
	@JsonProperty("close-amount")
	public long closeAmount;
	
	@JsonProperty("amount")
	public long amount;
	
	@JsonProperty("receiver")
	public String receiver;
}
