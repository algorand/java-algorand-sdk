package com.algorand.indexer.schemas;

import com.algorand.indexer.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;

public class TransactionPayment {
	
	public String closeRemainderTo; 
	public long closeAmount;
	public long amount;
	public String receiver;
	
	public TransactionPayment (String json) {
		JsonNode node = Utils.getRoot(json);
		
		this.closeRemainderTo = Utils.getString("close-remainder-to", node);
		this.closeAmount = Utils.getLong("close-amount", node);
		this.amount = Utils.getLong("amount", node);
		this.receiver = Utils.getString("receiver", node);
	}
}
