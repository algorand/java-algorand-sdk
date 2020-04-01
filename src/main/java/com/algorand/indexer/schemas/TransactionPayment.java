package com.algorand.indexer.schemas;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	Fields for a payment transaction. Definition: data/transactions/payment.go : 
	PaymentTxnFields 
 */
public class TransactionPayment {

	/*
		(close) when set, indicates that the sending account should be closed and all 
		remaining funds be transferred to this address. 
	 */
	@JsonProperty("close-remainder-to")
	public String closeRemainderTo;

	/*
		Number of MicroAlgos that were sent to the close-remainder-to address when 
		closing the sender account. 
	 */
	@JsonProperty("close-amount")
	public long closeAmount;

	/*
		(amt) number of MicroAlgos intended to be transferred. 
	 */
	@JsonProperty("amount")
	public long amount;

	/*
		(rcv) receiver's address. 
	 */
	@JsonProperty("receiver")
	public String receiver;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		TransactionPayment other = (TransactionPayment) o;
		if (!Objects.deepEquals(this.closeRemainderTo, other.closeRemainderTo)) return false;
		if (!Objects.deepEquals(this.closeAmount, other.closeAmount)) return false;
		if (!Objects.deepEquals(this.amount, other.amount)) return false;
		if (!Objects.deepEquals(this.receiver, other.receiver)) return false;

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
