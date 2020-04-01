package com.algorand.indexer.schemas;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	Fields for an asset transfer transaction. Definition: data/transactions/asset.go 
	: AssetTransferTxnFields 
 */
public class TransactionAssetTransfer {

	/*
		(aamt) Amount of asset to transfer. A zero amount transferred to self allocates 
		that asset in the account's Assets map. 
	 */
	@JsonProperty("amount")
	public long amount;

	/*
		(xaid) ID of the asset being transferred. 
	 */
	@JsonProperty("asset-id")
	public long assetId;

	/*
		(asnd) The effective sender during a clawback transactions. If this is not a 
		zero value, the real transaction sender must be the Clawback address from the 
		AssetParams. 
	 */
	@JsonProperty("sender")
	public String sender;

	/*
		(aclose) Indicates that the asset should be removed from the account's Assets 
		map, and specifies where the remaining asset holdings should be transferred. 
		It's always valid to transfer remaining asset holdings to the creator account. 
	 */
	@JsonProperty("close-to")
	public String closeTo;

	/*
		(arcv) Recipient address of the transfer. 
	 */
	@JsonProperty("receiver")
	public String receiver;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		TransactionAssetTransfer other = (TransactionAssetTransfer) o;
		if (!Objects.deepEquals(this.amount, other.amount)) return false;
		if (!Objects.deepEquals(this.assetId, other.assetId)) return false;
		if (!Objects.deepEquals(this.sender, other.sender)) return false;
		if (!Objects.deepEquals(this.closeTo, other.closeTo)) return false;
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
