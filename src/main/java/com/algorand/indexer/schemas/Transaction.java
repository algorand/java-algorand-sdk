package com.algorand.indexer.schemas;

import com.algorand.indexer.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;

public class Transaction {

	public TransactionAssetTransfer assetTransferTransaction;
	public String note;
	public long lastValid;
	public String group;
	public String poolError;
	public String sender;
	public String id;
	public String genesisHash;
	public long fee;
	public long closeRewards;
	public long roundTime;
	public long firstValid;
	public String genesisId;
	public TransactionAssetFreeze assetFreezeTransaction;
	public long senderRewards;
	public long closingAmount;
	public TransactionPayment paymentTransaction;
	public TransactionSignature signature;
	public long intraRoundOffset;
	public long confirmedRound;
	public long createdAssetIndex;
	public String lease;
	public TransactionAssetConfig assetConfigTransaction;
	public String type;
	public TransactionKeyreg keyregTransaction;
	public Long receiverRewards;
	
	public Transaction(String json) {
		JsonNode node = Utils.getRoot(json);
		
		this.assetTransferTransaction = new TransactionAssetTransfer(
				Utils.getNode("asset-transfer-transaction", node));
		this.note = Utils.getString("note", node);
		this.lastValid = Utils.getLong("last-valid", node);
		this.group = Utils.getBase64String("group", node);
		this.poolError = Utils.getString("pool-error", node);
		this.sender = Utils.getString("sender", node);
		this.id = Utils.getString("id", node);
		this.genesisHash = Utils.getString("genesis-hash", node);
		this.fee = Utils.getLong("fee", node);
		this.closeRewards = Utils.getLong("close-rewards", node);
		this.roundTime = Utils.getLong("pathName", node);
		this.firstValid = Utils.getLong("first-valid", node);
		this.genesisId = Utils.getString("genesis-id", node);
		this.assetFreezeTransaction = new TransactionAssetFreeze(
				Utils.getNode("asset-freeze-transaction", node));
		this.senderRewards = Utils.getLong("sender-rewards", node);
		this.closingAmount = Utils.getLong("closing-amount", node);
		this.paymentTransaction = new TransactionPayment(
				Utils.getNode("payment-transaction", node));
		this.signature = new TransactionSignature(
				Utils.getNode("signature", node));
		this.intraRoundOffset = Utils.getLong("intra-round-offset", node);
		this.confirmedRound = Utils.getLong("confirmed-round", node);
		this.createdAssetIndex = Utils.getLong("created-asset-index", node);
		this.lease = Utils.getBase64String("lease", node);
		this.assetConfigTransaction = new TransactionAssetConfig(
				Utils.getNode("asset-config-transaction", node));
		this.type = Utils.getString("type", node);
		this.keyregTransaction = new TransactionKeyreg(
				Utils.getNode("keyreg-transaction", node));
			this.receiverRewards = Utils.getLong("receiver-rewards", node);
				
	}
}
