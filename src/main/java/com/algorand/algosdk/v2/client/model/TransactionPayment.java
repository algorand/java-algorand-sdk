package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Fields for a payment transaction. Definition: data/transactions/payment.go : 
 * PaymentTxnFields 
 */
public class TransactionPayment {

	/**
	 * (amt) number of MicroAlgos intended to be transferred. 
	 */
	private long amount;
	private boolean amountIsSet;
	@JsonProperty("amount")
	public void setAmount(long amount){
		this.amount = amount;
		amountIsSet = true;
	}
	@JsonProperty("amount")
	public Long getAmount(){
		return amountIsSet ? amount : null;
	}
	/**
	 * Check if has a value for amount 
	 */	@JsonIgnore
	public boolean hasAmount(){
		return amountIsSet;
	}

	/**
	 * Number of MicroAlgos that were sent to the close-remainder-to address when 
	 * closing the sender account. 
	 */
	private long closeAmount;
	private boolean closeAmountIsSet;
	@JsonProperty("close-amount")
	public void setCloseAmount(long closeAmount){
		this.closeAmount = closeAmount;
		closeAmountIsSet = true;
	}
	@JsonProperty("close-amount")
	public Long getCloseAmount(){
		return closeAmountIsSet ? closeAmount : null;
	}
	/**
	 * Check if has a value for closeAmount 
	 */	@JsonIgnore
	public boolean hasCloseAmount(){
		return closeAmountIsSet;
	}

	/**
	 * (close) when set, indicates that the sending account should be closed and all 
	 * remaining funds be transferred to this address. 
	 */
	private String closeRemainderTo;
	private boolean closeRemainderToIsSet;
	@JsonProperty("close-remainder-to")
	public void setCloseRemainderTo(String closeRemainderTo){
		this.closeRemainderTo = closeRemainderTo;
		closeRemainderToIsSet = true;
	}
	@JsonProperty("close-remainder-to")
	public String getCloseRemainderTo(){
		return closeRemainderToIsSet ? closeRemainderTo : null;
	}
	/**
	 * Check if has a value for closeRemainderTo 
	 */	@JsonIgnore
	public boolean hasCloseRemainderTo(){
		return closeRemainderToIsSet;
	}

	/**
	 * (rcv) receiver's address. 
	 */
	private String receiver;
	private boolean receiverIsSet;
	@JsonProperty("receiver")
	public void setReceiver(String receiver){
		this.receiver = receiver;
		receiverIsSet = true;
	}
	@JsonProperty("receiver")
	public String getReceiver(){
		return receiverIsSet ? receiver : null;
	}
	/**
	 * Check if has a value for receiver 
	 */	@JsonIgnore
	public boolean hasReceiver(){
		return receiverIsSet;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		TransactionPayment other = (TransactionPayment) o;
		if (!Objects.deepEquals(this.amount, other.amount)) return false;
		if (!Objects.deepEquals(this.closeAmount, other.closeAmount)) return false;
		if (!Objects.deepEquals(this.closeRemainderTo, other.closeRemainderTo)) return false;
		if (!Objects.deepEquals(this.receiver, other.receiver)) return false;

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
