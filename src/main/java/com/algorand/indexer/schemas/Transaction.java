package com.algorand.indexer.schemas;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	Contains all fields common to all transactions and serves as an envelope to all 
	transactions type. Definition: data/transactions/signedtxn.go : SignedTxn 
	data/transactions/transaction.go : Transaction 
 */
public class Transaction {

	@JsonProperty("asset-transfer-transaction")
	public TransactionAssetTransfer assetTransferTransaction;

	/*
		(note) Free form data. 
	 */
	@JsonProperty("note")
	public String note;

	/*
		(lv) Last valid round for this transaction. 
	 */
	@JsonProperty("last-valid")
	public long lastValid;

	/*
		(grp) Base64 encoded byte array of a sha512/256 digest. When present indicates 
		that this transaction is part of a transaction group and the value is the 
		sha512/256 hash of the transactions in that group. 
	 */
	@JsonProperty("group")
	public String group;

	/*
		Part of algod API only. Indicates the transaction was evicted from this node's 
		transaction pool (if non-empty). A non-empty PoolError does not guarantee that 
		the transaction will never be committed; other nodes may not have evicted the 
		transaction and may attempt to commit it in the future. 
	 */
	@JsonProperty("pool-error")
	public String poolError;

	/*
		(snd) Sender's address. 
	 */
	@JsonProperty("sender")
	public String sender;

	/*
		Transaction ID 
	 */
	@JsonProperty("id")
	public String id;

	/*
		(gh) Hash of genesis block. 
	 */
	@JsonProperty("genesis-hash")
	public String genesisHash;

	/*
		(fee) Transaction fee. 
	 */
	@JsonProperty("fee")
	public long fee;

	/*
		(rc) rewards applied to close-remainder-to account. 
	 */
	@JsonProperty("close-rewards")
	public long closeRewards;

	/*
		Time when the block this transaction is in was confirmed. 
	 */
	@JsonProperty("round-time")
	public long roundTime;

	/*
		(fv) First valid round for this transaction. 
	 */
	@JsonProperty("first-valid")
	public long firstValid;

	/*
		(gen) genesis block ID. 
	 */
	@JsonProperty("genesis-id")
	public String genesisId;

	@JsonProperty("asset-freeze-transaction")
	public TransactionAssetFreeze assetFreezeTransaction;

	/*
		(rs) rewards applied to sender account. 
	 */
	@JsonProperty("sender-rewards")
	public long senderRewards;

	/*
		(ca) closing amount for transaction. 
	 */
	@JsonProperty("closing-amount")
	public long closingAmount;

	@JsonProperty("payment-transaction")
	public TransactionPayment paymentTransaction;

	@JsonProperty("signature")
	public TransactionSignature signature;

	/*
		Offset into the round where this transaction was confirmed. 
	 */
	@JsonProperty("intra-round-offset")
	public long intraRoundOffset;

	/*
		Round when the transaction was confirmed. 
	 */
	@JsonProperty("confirmed-round")
	public long confirmedRound;

	/*
		Specifies an asset index (ID) if an asset was created with this transaction. 
	 */
	@JsonProperty("created-asset-index")
	public long createdAssetIndex;

	/*
		(lx) Base64 encoded 32-byte array. Lease enforces mutual exclusion of 
		transactions. If this field is nonzero, then once the transaction is confirmed, 
		it acquires the lease identified by the (Sender, Lease) pair of the transaction 
		until the LastValid round passes. While this transaction possesses the lease, no 
		other transaction specifying this lease can be confirmed. 
	 */
	@JsonProperty("lease")
	public String lease;

	@JsonProperty("asset-config-transaction")
	public TransactionAssetConfig assetConfigTransaction;

	/*
		(type) Indicates what type of transaction this is. Different types have 
		different fields. Valid types, and where their fields are stored: * (pay) 
		payment-transaction * (keyreg) keyreg-transaction * (acfg) 
		asset-config-transaction * (axfer) asset-transfer-transaction * (afrz) 
		asset-freeze-transaction 
	 */
	@JsonProperty("type")
	public String type;

	@JsonProperty("keyreg-transaction")
	public TransactionKeyreg keyregTransaction;

	/*
		(rr) rewards applied to receiver account. 
	 */
	@JsonProperty("receiver-rewards")
	public long receiverRewards;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		Transaction other = (Transaction) o;
		if (!Objects.deepEquals(this.assetTransferTransaction, other.assetTransferTransaction)) return false;
		if (!Objects.deepEquals(this.note, other.note)) return false;
		if (!Objects.deepEquals(this.lastValid, other.lastValid)) return false;
		if (!Objects.deepEquals(this.group, other.group)) return false;
		if (!Objects.deepEquals(this.poolError, other.poolError)) return false;
		if (!Objects.deepEquals(this.sender, other.sender)) return false;
		if (!Objects.deepEquals(this.id, other.id)) return false;
		if (!Objects.deepEquals(this.genesisHash, other.genesisHash)) return false;
		if (!Objects.deepEquals(this.fee, other.fee)) return false;
		if (!Objects.deepEquals(this.closeRewards, other.closeRewards)) return false;
		if (!Objects.deepEquals(this.roundTime, other.roundTime)) return false;
		if (!Objects.deepEquals(this.firstValid, other.firstValid)) return false;
		if (!Objects.deepEquals(this.genesisId, other.genesisId)) return false;
		if (!Objects.deepEquals(this.assetFreezeTransaction, other.assetFreezeTransaction)) return false;
		if (!Objects.deepEquals(this.senderRewards, other.senderRewards)) return false;
		if (!Objects.deepEquals(this.closingAmount, other.closingAmount)) return false;
		if (!Objects.deepEquals(this.paymentTransaction, other.paymentTransaction)) return false;
		if (!Objects.deepEquals(this.signature, other.signature)) return false;
		if (!Objects.deepEquals(this.intraRoundOffset, other.intraRoundOffset)) return false;
		if (!Objects.deepEquals(this.confirmedRound, other.confirmedRound)) return false;
		if (!Objects.deepEquals(this.createdAssetIndex, other.createdAssetIndex)) return false;
		if (!Objects.deepEquals(this.lease, other.lease)) return false;
		if (!Objects.deepEquals(this.assetConfigTransaction, other.assetConfigTransaction)) return false;
		if (!Objects.deepEquals(this.type, other.type)) return false;
		if (!Objects.deepEquals(this.keyregTransaction, other.keyregTransaction)) return false;
		if (!Objects.deepEquals(this.receiverRewards, other.receiverRewards)) return false;

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
