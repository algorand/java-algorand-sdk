package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Fields for an asset transfer transaction. Definition: data/transactions/asset.go 
 * : AssetTransferTxnFields 
 */
public class TransactionAssetTransfer extends PathResponse {

	/**
	 * (aamt) Amount of asset to transfer. A zero amount transferred to self allocates 
	 * that asset in the account's Assets map. 
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
	 * (xaid) ID of the asset being transferred. 
	 */
	private long assetId;
	private boolean assetIdIsSet;
	@JsonProperty("asset-id")
	public void setAssetId(long assetId){
		this.assetId = assetId;
		assetIdIsSet = true;
	}
	@JsonProperty("asset-id")
	public Long getAssetId(){
		return assetIdIsSet ? assetId : null;
	}
	/**
	 * Check if has a value for assetId 
	 */	@JsonIgnore
	public boolean hasAssetId(){
		return assetIdIsSet;
	}

	/**
	 * Number of assets transfered to the close-to account as part of the transaction. 
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
	 * (aclose) Indicates that the asset should be removed from the account's Assets 
	 * map, and specifies where the remaining asset holdings should be transferred. 
	 * It's always valid to transfer remaining asset holdings to the creator account. 
	 */
	private String closeTo;
	private boolean closeToIsSet;
	@JsonProperty("close-to")
	public void setCloseTo(String closeTo){
		this.closeTo = closeTo;
		closeToIsSet = true;
	}
	@JsonProperty("close-to")
	public String getCloseTo(){
		return closeToIsSet ? closeTo : null;
	}
	/**
	 * Check if has a value for closeTo 
	 */	@JsonIgnore
	public boolean hasCloseTo(){
		return closeToIsSet;
	}

	/**
	 * (arcv) Recipient address of the transfer. 
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

	/**
	 * (asnd) The effective sender during a clawback transactions. If this is not a 
	 * zero value, the real transaction sender must be the Clawback address from the 
	 * AssetParams. 
	 */
	private String sender;
	private boolean senderIsSet;
	@JsonProperty("sender")
	public void setSender(String sender){
		this.sender = sender;
		senderIsSet = true;
	}
	@JsonProperty("sender")
	public String getSender(){
		return senderIsSet ? sender : null;
	}
	/**
	 * Check if has a value for sender 
	 */	@JsonIgnore
	public boolean hasSender(){
		return senderIsSet;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		TransactionAssetTransfer other = (TransactionAssetTransfer) o;
		if (!Objects.deepEquals(this.amount, other.amount)) return false;
		if (!Objects.deepEquals(this.assetId, other.assetId)) return false;
		if (!Objects.deepEquals(this.closeAmount, other.closeAmount)) return false;
		if (!Objects.deepEquals(this.closeTo, other.closeTo)) return false;
		if (!Objects.deepEquals(this.receiver, other.receiver)) return false;
		if (!Objects.deepEquals(this.sender, other.sender)) return false;

		return true;
	}
}
