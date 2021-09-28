package com.algorand.algosdk.v2.client.model;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains all fields common to all transactions and serves as an envelope to all
 * transactions type. Represents both regular and inner transactions.
 * Definition:
 * data/transactions/signedtxn.go : SignedTxn
 * data/transactions/transaction.go : Transaction
 *
 */
public class Transaction extends PathResponse {

    /**
     * Fields for application transactions.
     * Definition:
     * data/transactions/application.go : ApplicationCallTxnFields
     */
    @JsonProperty("application-transaction")
    public TransactionApplication applicationTransaction;

    /**
     * Fields for asset allocation, re-configuration, and destruction.
     * A zero value for asset-id indicates asset creation.
     * A zero value for the params indicates asset destruction.
     * Definition:
     * data/transactions/asset.go : AssetConfigTxnFields
     */
    @JsonProperty("asset-config-transaction")
    public TransactionAssetConfig assetConfigTransaction;

    /**
     * Fields for an asset freeze transaction.
     * Definition:
     * data/transactions/asset.go : AssetFreezeTxnFields
     */
    @JsonProperty("asset-freeze-transaction")
    public TransactionAssetFreeze assetFreezeTransaction;

    /**
     * Fields for an asset transfer transaction.
     * Definition:
     * data/transactions/asset.go : AssetTransferTxnFields
     */
    @JsonProperty("asset-transfer-transaction")
    public TransactionAssetTransfer assetTransferTransaction;

    /**
     * (sgnr) this is included with signed transactions when the signing address does
     * not equal the sender. The backend can use this to ensure that auth addr is equal
     * to the accounts auth addr.
     */
    @JsonProperty("auth-addr")
    public void authAddr(String authAddr) throws NoSuchAlgorithmException {
        this.authAddr = new Address(authAddr);
    }
    @JsonProperty("auth-addr")
    public String authAddr() throws NoSuchAlgorithmException {
        if (this.authAddr != null) {
            return this.authAddr.encodeAsString();
        } else {
            return null;
        }
    }
    public Address authAddr;

    /**
     * (rc) rewards applied to close-remainder-to account.
     */
    @JsonProperty("close-rewards")
    public Long closeRewards;

    /**
     * (ca) closing amount for transaction.
     */
    @JsonProperty("closing-amount")
    public Long closingAmount;

    /**
     * Round when the transaction was confirmed.
     */
    @JsonProperty("confirmed-round")
    public Long confirmedRound;

    /**
     * Specifies an application index (ID) if an application was created with this
     * transaction.
     */
    @JsonProperty("created-application-index")
    public Long createdApplicationIndex;

    /**
     * Specifies an asset index (ID) if an asset was created with this transaction.
     */
    @JsonProperty("created-asset-index")
    public Long createdAssetIndex;

    /**
     * (fee) Transaction fee.
     */
    @JsonProperty("fee")
    public Long fee;

    /**
     * (fv) First valid round for this transaction.
     */
    @JsonProperty("first-valid")
    public Long firstValid;

    /**
     * (gh) Hash of genesis block.
     */
    @JsonProperty("genesis-hash")
    public void genesisHash(String base64Encoded) {
        this.genesisHash = Encoder.decodeFromBase64(base64Encoded);
    }
    @JsonProperty("genesis-hash")
    public String genesisHash() {
        return Encoder.encodeToBase64(this.genesisHash);
    }
    public byte[] genesisHash;

    /**
     * (gen) genesis block ID.
     */
    @JsonProperty("genesis-id")
    public String genesisId;

    /**
     * (gd) Global state key/value changes for the application being executed by this
     * transaction.
     */
    @JsonProperty("global-state-delta")
    public List<EvalDeltaKeyValue> globalStateDelta = new ArrayList<EvalDeltaKeyValue>();

    /**
     * (grp) Base64 encoded byte array of a sha512/256 digest. When present indicates
     * that this transaction is part of a transaction group and the value is the
     * sha512/256 hash of the transactions in that group.
     */
    @JsonProperty("group")
    public void group(String base64Encoded) {
        this.group = Encoder.decodeFromBase64(base64Encoded);
    }
    @JsonProperty("group")
    public String group() {
        return Encoder.encodeToBase64(this.group);
    }
    public byte[] group;

    /**
     * Transaction ID
     */
    @JsonProperty("id")
    public String id;

    /**
     * Inner transactions produced by application execution.
     */
    @JsonProperty("inner-txns")
    public List<Transaction> innerTxns = new ArrayList<Transaction>();

    /**
     * Offset into the round where this transaction was confirmed.
     */
    @JsonProperty("intra-round-offset")
    public Long intraRoundOffset;

    /**
     * Fields for a keyreg transaction.
     * Definition:
     * data/transactions/keyreg.go : KeyregTxnFields
     */
    @JsonProperty("keyreg-transaction")
    public TransactionKeyreg keyregTransaction;

    /**
     * (lv) Last valid round for this transaction.
     */
    @JsonProperty("last-valid")
    public Long lastValid;

    /**
     * (lx) Base64 encoded 32-byte array. Lease enforces mutual exclusion of
     * transactions. If this field is nonzero, then once the transaction is confirmed,
     * it acquires the lease identified by the (Sender, Lease) pair of the transaction
     * until the LastValid round passes. While this transaction possesses the lease, no
     * other transaction specifying this lease can be confirmed.
     */
    @JsonProperty("lease")
    public void lease(String base64Encoded) {
        this.lease = Encoder.decodeFromBase64(base64Encoded);
    }
    @JsonProperty("lease")
    public String lease() {
        return Encoder.encodeToBase64(this.lease);
    }
    public byte[] lease;

    /**
     * (ld) Local state key/value changes for the application being executed by this
     * transaction.
     */
    @JsonProperty("local-state-delta")
    public List<AccountStateDelta> localStateDelta = new ArrayList<AccountStateDelta>();

    /**
     * (lg) Logs for the application being executed by this transaction.
     */
    @JsonProperty("logs")
    public void logs(List<String> base64Encoded) {
         this.logs = new ArrayList<byte[]>();
         for (String val : base64Encoded) {
             this.logs.add(Encoder.decodeFromBase64(val));
         }
     }
     @JsonProperty("logs")
     public List<String> logs() {
         ArrayList<String> ret = new ArrayList<String>();
         for (byte[] val : this.logs) {
             ret.add(Encoder.encodeToBase64(val));
         }
         return ret; 
     }
    public List<byte[]> logs;

    /**
     * (note) Free form data.
     */
    @JsonProperty("note")
    public void note(String base64Encoded) {
        this.note = Encoder.decodeFromBase64(base64Encoded);
    }
    @JsonProperty("note")
    public String note() {
        return Encoder.encodeToBase64(this.note);
    }
    public byte[] note;

    /**
     * Fields for a payment transaction.
     * Definition:
     * data/transactions/payment.go : PaymentTxnFields
     */
    @JsonProperty("payment-transaction")
    public TransactionPayment paymentTransaction;

    /**
     * (rr) rewards applied to receiver account.
     */
    @JsonProperty("receiver-rewards")
    public Long receiverRewards;

    /**
     * (rekey) when included in a valid transaction, the accounts auth addr will be
     * updated with this value and future signatures must be signed with the key
     * represented by this address.
     */
    @JsonProperty("rekey-to")
    public void rekeyTo(String rekeyTo) throws NoSuchAlgorithmException {
        this.rekeyTo = new Address(rekeyTo);
    }
    @JsonProperty("rekey-to")
    public String rekeyTo() throws NoSuchAlgorithmException {
        if (this.rekeyTo != null) {
            return this.rekeyTo.encodeAsString();
        } else {
            return null;
        }
    }
    public Address rekeyTo;

    /**
     * Time when the block this transaction is in was confirmed.
     */
    @JsonProperty("round-time")
    public Long roundTime;

    /**
     * (snd) Sender's address.
     */
    @JsonProperty("sender")
    public String sender;

    /**
     * (rs) rewards applied to sender account.
     */
    @JsonProperty("sender-rewards")
    public Long senderRewards;

    /**
     * Validation signature associated with some data. Only one of the signatures
     * should be provided.
     */
    @JsonProperty("signature")
    public TransactionSignature signature;

    /**
     * (type) Indicates what type of transaction this is. Different types have
     * different fields.
     * Valid types, and where their fields are stored:
     *   (pay) payment-transaction
     *   (keyreg) keyreg-transaction
     *   (acfg) asset-config-transaction
     *   (axfer) asset-transfer-transaction
     *   (afrz) asset-freeze-transaction
     *   (appl) application-transaction
     */
    @JsonProperty("tx-type")
    public Enums.TxType txType;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        Transaction other = (Transaction) o;
        if (!Objects.deepEquals(this.applicationTransaction, other.applicationTransaction)) return false;
        if (!Objects.deepEquals(this.assetConfigTransaction, other.assetConfigTransaction)) return false;
        if (!Objects.deepEquals(this.assetFreezeTransaction, other.assetFreezeTransaction)) return false;
        if (!Objects.deepEquals(this.assetTransferTransaction, other.assetTransferTransaction)) return false;
        if (!Objects.deepEquals(this.authAddr, other.authAddr)) return false;
        if (!Objects.deepEquals(this.closeRewards, other.closeRewards)) return false;
        if (!Objects.deepEquals(this.closingAmount, other.closingAmount)) return false;
        if (!Objects.deepEquals(this.confirmedRound, other.confirmedRound)) return false;
        if (!Objects.deepEquals(this.createdApplicationIndex, other.createdApplicationIndex)) return false;
        if (!Objects.deepEquals(this.createdAssetIndex, other.createdAssetIndex)) return false;
        if (!Objects.deepEquals(this.fee, other.fee)) return false;
        if (!Objects.deepEquals(this.firstValid, other.firstValid)) return false;
        if (!Objects.deepEquals(this.genesisHash, other.genesisHash)) return false;
        if (!Objects.deepEquals(this.genesisId, other.genesisId)) return false;
        if (!Objects.deepEquals(this.globalStateDelta, other.globalStateDelta)) return false;
        if (!Objects.deepEquals(this.group, other.group)) return false;
        if (!Objects.deepEquals(this.id, other.id)) return false;
        if (!Objects.deepEquals(this.innerTxns, other.innerTxns)) return false;
        if (!Objects.deepEquals(this.intraRoundOffset, other.intraRoundOffset)) return false;
        if (!Objects.deepEquals(this.keyregTransaction, other.keyregTransaction)) return false;
        if (!Objects.deepEquals(this.lastValid, other.lastValid)) return false;
        if (!Objects.deepEquals(this.lease, other.lease)) return false;
        if (!Objects.deepEquals(this.localStateDelta, other.localStateDelta)) return false;
        if (!Objects.deepEquals(this.logs, other.logs)) return false;
        if (!Objects.deepEquals(this.note, other.note)) return false;
        if (!Objects.deepEquals(this.paymentTransaction, other.paymentTransaction)) return false;
        if (!Objects.deepEquals(this.receiverRewards, other.receiverRewards)) return false;
        if (!Objects.deepEquals(this.rekeyTo, other.rekeyTo)) return false;
        if (!Objects.deepEquals(this.roundTime, other.roundTime)) return false;
        if (!Objects.deepEquals(this.sender, other.sender)) return false;
        if (!Objects.deepEquals(this.senderRewards, other.senderRewards)) return false;
        if (!Objects.deepEquals(this.signature, other.signature)) return false;
        if (!Objects.deepEquals(this.txType, other.txType)) return false;

        return true;
    }
}
