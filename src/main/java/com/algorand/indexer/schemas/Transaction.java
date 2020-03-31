package com.algorand.indexer.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Transaction {

	@JsonProperty("asset-transfer-transaction")
	public TransactionAssetTransfer assetTransferTransaction;

	@JsonProperty("note")
	public String note;

	@JsonProperty("last-valid")
	public long lastValid;

	@JsonProperty("group")
	public String group;

	@JsonProperty("pool-error")
	public String poolError;

	@JsonProperty("sender")
	public String sender;

	@JsonProperty("id")
	public String id;

	@JsonProperty("genesis-hash")
	public String genesisHash;

	@JsonProperty("fee")
	public long fee;

	@JsonProperty("close-rewards")
	public long closeRewards;

	@JsonProperty("round-time")
	public long roundTime;

	@JsonProperty("first-valid")
	public long firstValid;

	@JsonProperty("genesis-id")
	public String genesisId;

	@JsonProperty("asset-freeze-transaction")
	public TransactionAssetFreeze assetFreezeTransaction;

	@JsonProperty("sender-rewards")
	public long senderRewards;

	@JsonProperty("closing-amount")
	public long closingAmount;

	@JsonProperty("payment-transaction")
	public TransactionPayment paymentTransaction;

	@JsonProperty("signature")
	public TransactionSignature signature;

	@JsonProperty("intra-round-offset")
	public long intraRoundOffset;

	@JsonProperty("confirmed-round")
	public long confirmedRound;

	@JsonProperty("created-asset-index")
	public long createdAssetIndex;

	@JsonProperty("lease")
	public String lease;

	@JsonProperty("asset-config-transaction")
	public TransactionAssetConfig assetConfigTransaction;

	@JsonProperty("type")
	public String type;

	@JsonProperty("keyreg-transaction")
	public TransactionKeyreg keyregTransaction;

	@JsonProperty("receiver-rewards")
	public long receiverRewards;
}
