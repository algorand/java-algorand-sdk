package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PendingTransactionResponse extends PathResponse {

	/**
	 * The asset index if the transaction was found and it created an asset. 
	 */
	private long assetIndex;
	private boolean assetIndexIsSet;
	@JsonProperty("asset-index")
	public void setAssetIndex(long assetIndex){
		this.assetIndex = assetIndex;
		assetIndexIsSet = true;
	}
	@JsonProperty("asset-index")
	public Long getAssetIndex(){
		return assetIndexIsSet ? assetIndex : null;
	}
	/**
	 * Check if has a value for assetIndex 
	 */	@JsonIgnore
	public boolean hasAssetIndex(){
		return assetIndexIsSet;
	}

	/**
	 * Rewards in microalgos applied to the close remainder to account. 
	 */
	private long closeRewards;
	private boolean closeRewardsIsSet;
	@JsonProperty("close-rewards")
	public void setCloseRewards(long closeRewards){
		this.closeRewards = closeRewards;
		closeRewardsIsSet = true;
	}
	@JsonProperty("close-rewards")
	public Long getCloseRewards(){
		return closeRewardsIsSet ? closeRewards : null;
	}
	/**
	 * Check if has a value for closeRewards 
	 */	@JsonIgnore
	public boolean hasCloseRewards(){
		return closeRewardsIsSet;
	}

	/**
	 * Closing amount for the transaction. 
	 */
	private long closingAmount;
	private boolean closingAmountIsSet;
	@JsonProperty("closing-amount")
	public void setClosingAmount(long closingAmount){
		this.closingAmount = closingAmount;
		closingAmountIsSet = true;
	}
	@JsonProperty("closing-amount")
	public Long getClosingAmount(){
		return closingAmountIsSet ? closingAmount : null;
	}
	/**
	 * Check if has a value for closingAmount 
	 */	@JsonIgnore
	public boolean hasClosingAmount(){
		return closingAmountIsSet;
	}

	/**
	 * The round where this transaction was confirmed, if present. 
	 */
	private long confirmedRound;
	private boolean confirmedRoundIsSet;
	@JsonProperty("confirmed-round")
	public void setConfirmedRound(long confirmedRound){
		this.confirmedRound = confirmedRound;
		confirmedRoundIsSet = true;
	}
	@JsonProperty("confirmed-round")
	public Long getConfirmedRound(){
		return confirmedRoundIsSet ? confirmedRound : null;
	}
	/**
	 * Check if has a value for confirmedRound 
	 */	@JsonIgnore
	public boolean hasConfirmedRound(){
		return confirmedRoundIsSet;
	}

	/**
	 * Indicates that the transaction was kicked out of this node's transaction pool 
	 * (and specifies why that happened). An empty string indicates the transaction 
	 * wasn't kicked out of this node's txpool due to an error. 
	 */
	private String poolError;
	private boolean poolErrorIsSet;
	@JsonProperty("pool-error")
	public void setPoolError(String poolError){
		this.poolError = poolError;
		poolErrorIsSet = true;
	}
	@JsonProperty("pool-error")
	public String getPoolError(){
		return poolErrorIsSet ? poolError : null;
	}
	/**
	 * Check if has a value for poolError 
	 */	@JsonIgnore
	public boolean hasPoolError(){
		return poolErrorIsSet;
	}

	/**
	 * Rewards in microalgos applied to the receiver account. 
	 */
	private long receiverRewards;
	private boolean receiverRewardsIsSet;
	@JsonProperty("receiver-rewards")
	public void setReceiverRewards(long receiverRewards){
		this.receiverRewards = receiverRewards;
		receiverRewardsIsSet = true;
	}
	@JsonProperty("receiver-rewards")
	public Long getReceiverRewards(){
		return receiverRewardsIsSet ? receiverRewards : null;
	}
	/**
	 * Check if has a value for receiverRewards 
	 */	@JsonIgnore
	public boolean hasReceiverRewards(){
		return receiverRewardsIsSet;
	}

	/**
	 * Rewards in microalgos applied to the sender account. 
	 */
	private long senderRewards;
	private boolean senderRewardsIsSet;
	@JsonProperty("sender-rewards")
	public void setSenderRewards(long senderRewards){
		this.senderRewards = senderRewards;
		senderRewardsIsSet = true;
	}
	@JsonProperty("sender-rewards")
	public Long getSenderRewards(){
		return senderRewardsIsSet ? senderRewards : null;
	}
	/**
	 * Check if has a value for senderRewards 
	 */	@JsonIgnore
	public boolean hasSenderRewards(){
		return senderRewardsIsSet;
	}

	/**
	 * The raw signed transaction. 
	 */
	private String txn;
	private boolean txnIsSet;
	@JsonProperty("txn")
	public void setTxn(String txn){
		this.txn = txn;
		txnIsSet = true;
	}
	@JsonProperty("txn")
	public String getTxn(){
		return txnIsSet ? txn : null;
	}
	/**
	 * Check if has a value for txn 
	 */	@JsonIgnore
	public boolean hasTxn(){
		return txnIsSet;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		PendingTransactionResponse other = (PendingTransactionResponse) o;
		if (!Objects.deepEquals(this.assetIndex, other.assetIndex)) return false;
		if (!Objects.deepEquals(this.closeRewards, other.closeRewards)) return false;
		if (!Objects.deepEquals(this.closingAmount, other.closingAmount)) return false;
		if (!Objects.deepEquals(this.confirmedRound, other.confirmedRound)) return false;
		if (!Objects.deepEquals(this.poolError, other.poolError)) return false;
		if (!Objects.deepEquals(this.receiverRewards, other.receiverRewards)) return false;
		if (!Objects.deepEquals(this.senderRewards, other.senderRewards)) return false;
		if (!Objects.deepEquals(this.txn, other.txn)) return false;

		return true;
	}
}
