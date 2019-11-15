/*
 * Algod REST API.
 * API Endpoint for AlgoD Operations.
 *
 * OpenAPI spec version: 0.0.1
 * Contact: contact@algorand.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package com.algorand.algosdk.algod.client.model;

import com.algorand.algosdk.algod.client.model.AssetConfigTransactionType;
import com.algorand.algosdk.algod.client.model.AssetFreezeTransactionType;
import com.algorand.algosdk.algod.client.model.AssetTransferTransactionType;
import com.algorand.algosdk.algod.client.model.KeyregTransactionType;
import com.algorand.algosdk.algod.client.model.PaymentTransactionType;
import com.algorand.algosdk.algod.client.model.TransactionResults;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.ObjectUtils;

/**
 * Transaction contains all fields common to all transactions and serves as an envelope to all transactions type
 */
@ApiModel(description = "Transaction contains all fields common to all transactions and serves as an envelope to all transactions type")

public class Transaction {
  @SerializedName("curcfg")
  private AssetConfigTransactionType curcfg = null;

  @SerializedName("curfrz")
  private AssetFreezeTransactionType curfrz = null;

  @SerializedName("curxfer")
  private AssetTransferTransactionType curxfer = null;

  @SerializedName("fee")
  private java.math.BigInteger fee = null;

  @SerializedName("first-round")
  private java.math.BigInteger firstRound = null;

  @SerializedName("from")
  private String from = null;

  @SerializedName("fromrewards")
  private java.math.BigInteger fromrewards = null;

  @SerializedName("genesisID")
  private String genesisID = null;

  @SerializedName("genesishashb64")
  private byte[] genesishashb64 = null;

  @SerializedName("group")
  private byte[] group = null;

  @SerializedName("keyreg")
  private KeyregTransactionType keyreg = null;

  @SerializedName("last-round")
  private java.math.BigInteger lastRound = null;

  @SerializedName("noteb64")
  private byte[] noteb64 = null;

  @SerializedName("payment")
  private PaymentTransactionType payment = null;

  @SerializedName("poolerror")
  private String poolerror = null;

  @SerializedName("round")
  private java.math.BigInteger round = null;

  @SerializedName("tx")
  private String tx = null;

  @SerializedName("txresults")
  private TransactionResults txresults = null;

  @SerializedName("type")
  private String type = null;

  public Transaction curcfg(AssetConfigTransactionType curcfg) {
    this.curcfg = curcfg;
    return this;
  }

   /**
   * Get curcfg
   * @return curcfg
  **/
  @ApiModelProperty(value = "")
  public AssetConfigTransactionType getCurcfg() {
    return curcfg;
  }

  public void setCurcfg(AssetConfigTransactionType curcfg) {
    this.curcfg = curcfg;
  }

  public Transaction curfrz(AssetFreezeTransactionType curfrz) {
    this.curfrz = curfrz;
    return this;
  }

   /**
   * Get curfrz
   * @return curfrz
  **/
  @ApiModelProperty(value = "")
  public AssetFreezeTransactionType getCurfrz() {
    return curfrz;
  }

  public void setCurfrz(AssetFreezeTransactionType curfrz) {
    this.curfrz = curfrz;
  }

  public Transaction curxfer(AssetTransferTransactionType curxfer) {
    this.curxfer = curxfer;
    return this;
  }

   /**
   * Get curxfer
   * @return curxfer
  **/
  @ApiModelProperty(value = "")
  public AssetTransferTransactionType getCurxfer() {
    return curxfer;
  }

  public void setCurxfer(AssetTransferTransactionType curxfer) {
    this.curxfer = curxfer;
  }

  public Transaction fee(java.math.BigInteger fee) {
    this.fee = fee;
    return this;
  }

   /**
   * Fee is the transaction fee
   * @return fee
  **/
  @ApiModelProperty(required = true, value = "Fee is the transaction fee")
  public java.math.BigInteger getFee() {
    return fee;
  }

  public void setFee(java.math.BigInteger fee) {
    this.fee = fee;
  }

  public Transaction firstRound(java.math.BigInteger firstRound) {
    this.firstRound = firstRound;
    return this;
  }

   /**
   * FirstRound indicates the first valid round for this transaction
   * @return firstRound
  **/
  @ApiModelProperty(required = true, value = "FirstRound indicates the first valid round for this transaction")
  public java.math.BigInteger getFirstRound() {
    return firstRound;
  }

  public void setFirstRound(java.math.BigInteger firstRound) {
    this.firstRound = firstRound;
  }

  public Transaction from(String from) {
    this.from = from;
    return this;
  }

   /**
   * From is the sender&#39;s address
   * @return from
  **/
  @ApiModelProperty(required = true, value = "From is the sender's address")
  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public Transaction fromrewards(java.math.BigInteger fromrewards) {
    this.fromrewards = fromrewards;
    return this;
  }

   /**
   * FromRewards is the amount of pending rewards applied to the From account as part of this transaction.
   * @return fromrewards
  **/
  @ApiModelProperty(value = "FromRewards is the amount of pending rewards applied to the From account as part of this transaction.")
  public java.math.BigInteger getFromrewards() {
    return fromrewards;
  }

  public void setFromrewards(java.math.BigInteger fromrewards) {
    this.fromrewards = fromrewards;
  }

  public Transaction genesisID(String genesisID) {
    this.genesisID = genesisID;
    return this;
  }

   /**
   * Genesis ID
   * @return genesisID
  **/
  @ApiModelProperty(required = true, value = "Genesis ID")
  public String getGenesisID() {
    return genesisID;
  }

  public void setGenesisID(String genesisID) {
    this.genesisID = genesisID;
  }

  public Transaction genesishashb64(byte[] genesishashb64) {
    this.genesishashb64 = genesishashb64;
    return this;
  }

   /**
   * Genesis hash
   * @return genesishashb64
  **/
  @ApiModelProperty(required = true, value = "Genesis hash")
  public byte[] getGenesishashb64() {
    return genesishashb64;
  }

  public void setGenesishashb64(byte[] genesishashb64) {
    this.genesishashb64 = genesishashb64;
  }

  public Transaction group(byte[] group) {
    this.group = group;
    return this;
  }

   /**
   * Group
   * @return group
  **/
  @ApiModelProperty(value = "Group")
  public byte[] getGroup() {
    return group;
  }

  public void setGroup(byte[] group) {
    this.group = group;
  }

  public Transaction keyreg(KeyregTransactionType keyreg) {
    this.keyreg = keyreg;
    return this;
  }

   /**
   * Get keyreg
   * @return keyreg
  **/
  @ApiModelProperty(value = "")
  public KeyregTransactionType getKeyreg() {
    return keyreg;
  }

  public void setKeyreg(KeyregTransactionType keyreg) {
    this.keyreg = keyreg;
  }

  public Transaction lastRound(java.math.BigInteger lastRound) {
    this.lastRound = lastRound;
    return this;
  }

   /**
   * LastRound indicates the last valid round for this transaction
   * @return lastRound
  **/
  @ApiModelProperty(required = true, value = "LastRound indicates the last valid round for this transaction")
  public java.math.BigInteger getLastRound() {
    return lastRound;
  }

  public void setLastRound(java.math.BigInteger lastRound) {
    this.lastRound = lastRound;
  }

  public Transaction noteb64(byte[] noteb64) {
    this.noteb64 = noteb64;
    return this;
  }

   /**
   * Note is a free form data
   * @return noteb64
  **/
  @ApiModelProperty(value = "Note is a free form data")
  public byte[] getNoteb64() {
    return noteb64;
  }

  public void setNoteb64(byte[] noteb64) {
    this.noteb64 = noteb64;
  }

  public Transaction payment(PaymentTransactionType payment) {
    this.payment = payment;
    return this;
  }

   /**
   * Get payment
   * @return payment
  **/
  @ApiModelProperty(value = "")
  public PaymentTransactionType getPayment() {
    return payment;
  }

  public void setPayment(PaymentTransactionType payment) {
    this.payment = payment;
  }

  public Transaction poolerror(String poolerror) {
    this.poolerror = poolerror;
    return this;
  }

   /**
   * PoolError indicates the transaction was evicted from this node&#39;s transaction pool (if non-empty).  A non-empty PoolError does not guarantee that the transaction will never be committed; other nodes may not have evicted the transaction and may attempt to commit it in the future.
   * @return poolerror
  **/
  @ApiModelProperty(value = "PoolError indicates the transaction was evicted from this node's transaction pool (if non-empty).  A non-empty PoolError does not guarantee that the transaction will never be committed; other nodes may not have evicted the transaction and may attempt to commit it in the future.")
  public String getPoolerror() {
    return poolerror;
  }

  public void setPoolerror(String poolerror) {
    this.poolerror = poolerror;
  }

  public Transaction round(java.math.BigInteger round) {
    this.round = round;
    return this;
  }

   /**
   * ConfirmedRound indicates the block number this transaction appeared in
   * @return round
  **/
  @ApiModelProperty(value = "ConfirmedRound indicates the block number this transaction appeared in")
  public java.math.BigInteger getRound() {
    return round;
  }

  public void setRound(java.math.BigInteger round) {
    this.round = round;
  }

  public Transaction tx(String tx) {
    this.tx = tx;
    return this;
  }

   /**
   * TxID is the transaction ID
   * @return tx
  **/
  @ApiModelProperty(required = true, value = "TxID is the transaction ID")
  public String getTx() {
    return tx;
  }

  public void setTx(String tx) {
    this.tx = tx;
  }

  public Transaction txresults(TransactionResults txresults) {
    this.txresults = txresults;
    return this;
  }

   /**
   * Get txresults
   * @return txresults
  **/
  @ApiModelProperty(value = "")
  public TransactionResults getTxresults() {
    return txresults;
  }

  public void setTxresults(TransactionResults txresults) {
    this.txresults = txresults;
  }

  public Transaction type(String type) {
    this.type = type;
    return this;
  }

   /**
   * Type is the transaction type
   * @return type
  **/
  @ApiModelProperty(required = true, value = "Type is the transaction type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }


  @Override
  public boolean equals(java.lang.Object o) {
  if (this == o) {
    return true;
  }
  if (o == null || getClass() != o.getClass()) {
    return false;
  }
    Transaction transaction = (Transaction) o;
    return ObjectUtils.equals(this.curcfg, transaction.curcfg) &&
    ObjectUtils.equals(this.curfrz, transaction.curfrz) &&
    ObjectUtils.equals(this.curxfer, transaction.curxfer) &&
    ObjectUtils.equals(this.fee, transaction.fee) &&
    ObjectUtils.equals(this.firstRound, transaction.firstRound) &&
    ObjectUtils.equals(this.from, transaction.from) &&
    ObjectUtils.equals(this.fromrewards, transaction.fromrewards) &&
    ObjectUtils.equals(this.genesisID, transaction.genesisID) &&
    ObjectUtils.equals(this.genesishashb64, transaction.genesishashb64) &&
    ObjectUtils.equals(this.group, transaction.group) &&
    ObjectUtils.equals(this.keyreg, transaction.keyreg) &&
    ObjectUtils.equals(this.lastRound, transaction.lastRound) &&
    ObjectUtils.equals(this.noteb64, transaction.noteb64) &&
    ObjectUtils.equals(this.payment, transaction.payment) &&
    ObjectUtils.equals(this.poolerror, transaction.poolerror) &&
    ObjectUtils.equals(this.round, transaction.round) &&
    ObjectUtils.equals(this.tx, transaction.tx) &&
    ObjectUtils.equals(this.txresults, transaction.txresults) &&
    ObjectUtils.equals(this.type, transaction.type);
  }

  @Override
  public int hashCode() {
    return ObjectUtils.hashCodeMulti(curcfg, curfrz, curxfer, fee, firstRound, from, fromrewards, genesisID, genesishashb64, group, keyreg, lastRound, noteb64, payment, poolerror, round, tx, txresults, type);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Transaction {\n");
    
    sb.append("    curcfg: ").append(toIndentedString(curcfg)).append("\n");
    sb.append("    curfrz: ").append(toIndentedString(curfrz)).append("\n");
    sb.append("    curxfer: ").append(toIndentedString(curxfer)).append("\n");
    sb.append("    fee: ").append(toIndentedString(fee)).append("\n");
    sb.append("    firstRound: ").append(toIndentedString(firstRound)).append("\n");
    sb.append("    from: ").append(toIndentedString(from)).append("\n");
    sb.append("    fromrewards: ").append(toIndentedString(fromrewards)).append("\n");
    sb.append("    genesisID: ").append(toIndentedString(genesisID)).append("\n");
    sb.append("    genesishashb64: ").append(toIndentedString(genesishashb64)).append("\n");
    sb.append("    group: ").append(toIndentedString(group)).append("\n");
    sb.append("    keyreg: ").append(toIndentedString(keyreg)).append("\n");
    sb.append("    lastRound: ").append(toIndentedString(lastRound)).append("\n");
    sb.append("    noteb64: ").append(toIndentedString(noteb64)).append("\n");
    sb.append("    payment: ").append(toIndentedString(payment)).append("\n");
    sb.append("    poolerror: ").append(toIndentedString(poolerror)).append("\n");
    sb.append("    round: ").append(toIndentedString(round)).append("\n");
    sb.append("    tx: ").append(toIndentedString(tx)).append("\n");
    sb.append("    txresults: ").append(toIndentedString(txresults)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

