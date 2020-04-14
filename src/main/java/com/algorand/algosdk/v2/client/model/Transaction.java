package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Contains all fields common to all transactions and serves as an envelope to all 
 * transactions type. Definition: data/transactions/signedtxn.go : SignedTxn 
 * data/transactions/transaction.go : Transaction 
 */
public class Transaction extends PathResponse {

	private TransactionAssetConfig assetConfigTransaction;
	private boolean assetConfigTransactionIsSet;
	@JsonProperty("asset-config-transaction")
	public void setAssetConfigTransaction(TransactionAssetConfig assetConfigTransaction){
		this.assetConfigTransaction = assetConfigTransaction;
		assetConfigTransactionIsSet = true;
	}
	@JsonProperty("asset-config-transaction")
	public TransactionAssetConfig getAssetConfigTransaction(){
		return assetConfigTransactionIsSet ? assetConfigTransaction : null;
	}
	/**
	 * Check if has a value for assetConfigTransaction 
	 */	@JsonIgnore
	public boolean hasAssetConfigTransaction(){
		return assetConfigTransactionIsSet;
	}

	private TransactionAssetFreeze assetFreezeTransaction;
	private boolean assetFreezeTransactionIsSet;
	@JsonProperty("asset-freeze-transaction")
	public void setAssetFreezeTransaction(TransactionAssetFreeze assetFreezeTransaction){
		this.assetFreezeTransaction = assetFreezeTransaction;
		assetFreezeTransactionIsSet = true;
	}
	@JsonProperty("asset-freeze-transaction")
	public TransactionAssetFreeze getAssetFreezeTransaction(){
		return assetFreezeTransactionIsSet ? assetFreezeTransaction : null;
	}
	/**
	 * Check if has a value for assetFreezeTransaction 
	 */	@JsonIgnore
	public boolean hasAssetFreezeTransaction(){
		return assetFreezeTransactionIsSet;
	}

	private TransactionAssetTransfer assetTransferTransaction;
	private boolean assetTransferTransactionIsSet;
	@JsonProperty("asset-transfer-transaction")
	public void setAssetTransferTransaction(TransactionAssetTransfer assetTransferTransaction){
		this.assetTransferTransaction = assetTransferTransaction;
		assetTransferTransactionIsSet = true;
	}
	@JsonProperty("asset-transfer-transaction")
	public TransactionAssetTransfer getAssetTransferTransaction(){
		return assetTransferTransactionIsSet ? assetTransferTransaction : null;
	}
	/**
	 * Check if has a value for assetTransferTransaction 
	 */	@JsonIgnore
	public boolean hasAssetTransferTransaction(){
		return assetTransferTransactionIsSet;
	}

	/**
	 * (rc) rewards applied to close-remainder-to account. 
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
	 * (ca) closing amount for transaction. 
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
	 * Round when the transaction was confirmed. 
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
	 * Specifies an asset index (ID) if an asset was created with this transaction. 
	 */
	private long createdAssetIndex;
	private boolean createdAssetIndexIsSet;
	@JsonProperty("created-asset-index")
	public void setCreatedAssetIndex(long createdAssetIndex){
		this.createdAssetIndex = createdAssetIndex;
		createdAssetIndexIsSet = true;
	}
	@JsonProperty("created-asset-index")
	public Long getCreatedAssetIndex(){
		return createdAssetIndexIsSet ? createdAssetIndex : null;
	}
	/**
	 * Check if has a value for createdAssetIndex 
	 */	@JsonIgnore
	public boolean hasCreatedAssetIndex(){
		return createdAssetIndexIsSet;
	}

	/**
	 * (fee) Transaction fee. 
	 */
	private long fee;
	private boolean feeIsSet;
	@JsonProperty("fee")
	public void setFee(long fee){
		this.fee = fee;
		feeIsSet = true;
	}
	@JsonProperty("fee")
	public Long getFee(){
		return feeIsSet ? fee : null;
	}
	/**
	 * Check if has a value for fee 
	 */	@JsonIgnore
	public boolean hasFee(){
		return feeIsSet;
	}

	/**
	 * (fv) First valid round for this transaction. 
	 */
	private long firstValid;
	private boolean firstValidIsSet;
	@JsonProperty("first-valid")
	public void setFirstValid(long firstValid){
		this.firstValid = firstValid;
		firstValidIsSet = true;
	}
	@JsonProperty("first-valid")
	public Long getFirstValid(){
		return firstValidIsSet ? firstValid : null;
	}
	/**
	 * Check if has a value for firstValid 
	 */	@JsonIgnore
	public boolean hasFirstValid(){
		return firstValidIsSet;
	}

	/**
	 * (gh) Hash of genesis block. 
	 */
	private String genesisHash;
	private boolean genesisHashIsSet;
	@JsonProperty("genesis-hash")
	public void setGenesisHash(String genesisHash){
		this.genesisHash = genesisHash;
		genesisHashIsSet = true;
	}
	@JsonProperty("genesis-hash")
	public String getGenesisHash(){
		return genesisHashIsSet ? genesisHash : null;
	}
	/**
	 * Check if has a value for genesisHash 
	 */	@JsonIgnore
	public boolean hasGenesisHash(){
		return genesisHashIsSet;
	}

	/**
	 * (gen) genesis block ID. 
	 */
	private String genesisId;
	private boolean genesisIdIsSet;
	@JsonProperty("genesis-id")
	public void setGenesisId(String genesisId){
		this.genesisId = genesisId;
		genesisIdIsSet = true;
	}
	@JsonProperty("genesis-id")
	public String getGenesisId(){
		return genesisIdIsSet ? genesisId : null;
	}
	/**
	 * Check if has a value for genesisId 
	 */	@JsonIgnore
	public boolean hasGenesisId(){
		return genesisIdIsSet;
	}

	/**
	 * (grp) Base64 encoded byte array of a sha512/256 digest. When present indicates 
	 * that this transaction is part of a transaction group and the value is the 
	 * sha512/256 hash of the transactions in that group. 
	 */
	private String group;
	private boolean groupIsSet;
	@JsonProperty("group")
	public void setGroup(String group){
		this.group = group;
		groupIsSet = true;
	}
	@JsonProperty("group")
	public String getGroup(){
		return groupIsSet ? group : null;
	}
	/**
	 * Check if has a value for group 
	 */	@JsonIgnore
	public boolean hasGroup(){
		return groupIsSet;
	}

	/**
	 * Transaction ID 
	 */
	private String id;
	private boolean idIsSet;
	@JsonProperty("id")
	public void setId(String id){
		this.id = id;
		idIsSet = true;
	}
	@JsonProperty("id")
	public String getId(){
		return idIsSet ? id : null;
	}
	/**
	 * Check if has a value for id 
	 */	@JsonIgnore
	public boolean hasId(){
		return idIsSet;
	}

	/**
	 * Offset into the round where this transaction was confirmed. 
	 */
	private long intraRoundOffset;
	private boolean intraRoundOffsetIsSet;
	@JsonProperty("intra-round-offset")
	public void setIntraRoundOffset(long intraRoundOffset){
		this.intraRoundOffset = intraRoundOffset;
		intraRoundOffsetIsSet = true;
	}
	@JsonProperty("intra-round-offset")
	public Long getIntraRoundOffset(){
		return intraRoundOffsetIsSet ? intraRoundOffset : null;
	}
	/**
	 * Check if has a value for intraRoundOffset 
	 */	@JsonIgnore
	public boolean hasIntraRoundOffset(){
		return intraRoundOffsetIsSet;
	}

	private TransactionKeyreg keyregTransaction;
	private boolean keyregTransactionIsSet;
	@JsonProperty("keyreg-transaction")
	public void setKeyregTransaction(TransactionKeyreg keyregTransaction){
		this.keyregTransaction = keyregTransaction;
		keyregTransactionIsSet = true;
	}
	@JsonProperty("keyreg-transaction")
	public TransactionKeyreg getKeyregTransaction(){
		return keyregTransactionIsSet ? keyregTransaction : null;
	}
	/**
	 * Check if has a value for keyregTransaction 
	 */	@JsonIgnore
	public boolean hasKeyregTransaction(){
		return keyregTransactionIsSet;
	}

	/**
	 * (lv) Last valid round for this transaction. 
	 */
	private long lastValid;
	private boolean lastValidIsSet;
	@JsonProperty("last-valid")
	public void setLastValid(long lastValid){
		this.lastValid = lastValid;
		lastValidIsSet = true;
	}
	@JsonProperty("last-valid")
	public Long getLastValid(){
		return lastValidIsSet ? lastValid : null;
	}
	/**
	 * Check if has a value for lastValid 
	 */	@JsonIgnore
	public boolean hasLastValid(){
		return lastValidIsSet;
	}

	/**
	 * (lx) Base64 encoded 32-byte array. Lease enforces mutual exclusion of 
	 * transactions. If this field is nonzero, then once the transaction is confirmed, 
	 * it acquires the lease identified by the (Sender, Lease) pair of the transaction 
	 * until the LastValid round passes. While this transaction possesses the lease, no 
	 * other transaction specifying this lease can be confirmed. 
	 */
	private String lease;
	private boolean leaseIsSet;
	@JsonProperty("lease")
	public void setLease(String lease){
		this.lease = lease;
		leaseIsSet = true;
	}
	@JsonProperty("lease")
	public String getLease(){
		return leaseIsSet ? lease : null;
	}
	/**
	 * Check if has a value for lease 
	 */	@JsonIgnore
	public boolean hasLease(){
		return leaseIsSet;
	}

	/**
	 * (note) Free form data. 
	 */
	private String note;
	private boolean noteIsSet;
	@JsonProperty("note")
	public void setNote(String note){
		this.note = note;
		noteIsSet = true;
	}
	@JsonProperty("note")
	public String getNote(){
		return noteIsSet ? note : null;
	}
	/**
	 * Check if has a value for note 
	 */	@JsonIgnore
	public boolean hasNote(){
		return noteIsSet;
	}

	private TransactionPayment paymentTransaction;
	private boolean paymentTransactionIsSet;
	@JsonProperty("payment-transaction")
	public void setPaymentTransaction(TransactionPayment paymentTransaction){
		this.paymentTransaction = paymentTransaction;
		paymentTransactionIsSet = true;
	}
	@JsonProperty("payment-transaction")
	public TransactionPayment getPaymentTransaction(){
		return paymentTransactionIsSet ? paymentTransaction : null;
	}
	/**
	 * Check if has a value for paymentTransaction 
	 */	@JsonIgnore
	public boolean hasPaymentTransaction(){
		return paymentTransactionIsSet;
	}

	/**
	 * (rr) rewards applied to receiver account. 
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
	 * Time when the block this transaction is in was confirmed. 
	 */
	private long roundTime;
	private boolean roundTimeIsSet;
	@JsonProperty("round-time")
	public void setRoundTime(long roundTime){
		this.roundTime = roundTime;
		roundTimeIsSet = true;
	}
	@JsonProperty("round-time")
	public Long getRoundTime(){
		return roundTimeIsSet ? roundTime : null;
	}
	/**
	 * Check if has a value for roundTime 
	 */	@JsonIgnore
	public boolean hasRoundTime(){
		return roundTimeIsSet;
	}

	/**
	 * (snd) Sender's address. 
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

	/**
	 * (rs) rewards applied to sender account. 
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

	private TransactionSignature signature;
	private boolean signatureIsSet;
	@JsonProperty("signature")
	public void setSignature(TransactionSignature signature){
		this.signature = signature;
		signatureIsSet = true;
	}
	@JsonProperty("signature")
	public TransactionSignature getSignature(){
		return signatureIsSet ? signature : null;
	}
	/**
	 * Check if has a value for signature 
	 */	@JsonIgnore
	public boolean hasSignature(){
		return signatureIsSet;
	}

	/**
	 * (type) Indicates what type of transaction this is. Different types have 
	 * different fields. Valid types, and where their fields are stored: * (pay) 
	 * payment-transaction * (keyreg) keyreg-transaction * (acfg) 
	 * asset-config-transaction * (axfer) asset-transfer-transaction * (afrz) 
	 * asset-freeze-transaction 
	 */
	private String type;
	private boolean typeIsSet;
	@JsonProperty("type")
	public void setType(String type){
		this.type = type;
		typeIsSet = true;
	}
	@JsonProperty("type")
	public String getType(){
		return typeIsSet ? type : null;
	}
	/**
	 * Check if has a value for type 
	 */	@JsonIgnore
	public boolean hasType(){
		return typeIsSet;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		Transaction other = (Transaction) o;
		if (!Objects.deepEquals(this.assetConfigTransaction, other.assetConfigTransaction)) return false;
		if (!Objects.deepEquals(this.assetFreezeTransaction, other.assetFreezeTransaction)) return false;
		if (!Objects.deepEquals(this.assetTransferTransaction, other.assetTransferTransaction)) return false;
		if (!Objects.deepEquals(this.closeRewards, other.closeRewards)) return false;
		if (!Objects.deepEquals(this.closingAmount, other.closingAmount)) return false;
		if (!Objects.deepEquals(this.confirmedRound, other.confirmedRound)) return false;
		if (!Objects.deepEquals(this.createdAssetIndex, other.createdAssetIndex)) return false;
		if (!Objects.deepEquals(this.fee, other.fee)) return false;
		if (!Objects.deepEquals(this.firstValid, other.firstValid)) return false;
		if (!Objects.deepEquals(this.genesisHash, other.genesisHash)) return false;
		if (!Objects.deepEquals(this.genesisId, other.genesisId)) return false;
		if (!Objects.deepEquals(this.group, other.group)) return false;
		if (!Objects.deepEquals(this.id, other.id)) return false;
		if (!Objects.deepEquals(this.intraRoundOffset, other.intraRoundOffset)) return false;
		if (!Objects.deepEquals(this.keyregTransaction, other.keyregTransaction)) return false;
		if (!Objects.deepEquals(this.lastValid, other.lastValid)) return false;
		if (!Objects.deepEquals(this.lease, other.lease)) return false;
		if (!Objects.deepEquals(this.note, other.note)) return false;
		if (!Objects.deepEquals(this.paymentTransaction, other.paymentTransaction)) return false;
		if (!Objects.deepEquals(this.receiverRewards, other.receiverRewards)) return false;
		if (!Objects.deepEquals(this.roundTime, other.roundTime)) return false;
		if (!Objects.deepEquals(this.sender, other.sender)) return false;
		if (!Objects.deepEquals(this.senderRewards, other.senderRewards)) return false;
		if (!Objects.deepEquals(this.signature, other.signature)) return false;
		if (!Objects.deepEquals(this.type, other.type)) return false;

		return true;
	}
}
