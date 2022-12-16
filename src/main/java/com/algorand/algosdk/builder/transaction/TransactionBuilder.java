package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.transaction.Lease;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

/**
 * TransactionBuilder has parameters common to all transactions types.
 */
@SuppressWarnings("unchecked")
public abstract class TransactionBuilder<T extends TransactionBuilder<T>> extends TransactionParametersBuilder<T> {
    protected final Transaction.Type type;

    protected Digest group = null;

    protected TransactionBuilder(Transaction.Type type) {
        this.type = type;
    }

    protected abstract void applyTo(Transaction txn);
    
    /**
     * Build the Transaction object. An exception is thrown if a valid transaction cannot be created with the provided
     * fields.
     * @return A transaction.
     */
    final public Transaction build() {

        if (lastValid == null && firstValid != null) {
            lastValid = firstValid.add(BigInteger.valueOf(1000));
        }

        Transaction txn = new Transaction();
        txn.type = type;
        applyTo(txn);

        if (sender != null) txn.sender = sender;
        if (firstValid != null) txn.firstValid = firstValid;
        if (lastValid != null) txn.lastValid = lastValid;
        if (note != null && note.length > 0) txn.note = note;
        if (rekeyTo != null) txn.rekeyTo = rekeyTo;
        if (genesisID != null) txn.genesisID = genesisID;
        if (genesisHash != null) txn.genesisHash = genesisHash;

        if (lease != null && lease.length != 0) {
            txn.setLease(new Lease(lease));
        }

        if(fee != null && flatFee != null) {
            throw new IllegalArgumentException("Cannot set both fee and flatFee.");
        }
        if(fee == null && flatFee == null){
            txn.fee = Account.MIN_TX_FEE_UALGOS;
            return txn;
        }
        if(fee != null) {
            try {
                Account.setFeeByFeePerByte(txn, fee);
            } catch (NoSuchAlgorithmException e) {
                throw new UnsupportedOperationException(e);
            }
            if (txn.fee == null || txn.fee.equals(BigInteger.valueOf(0))) {
                txn.fee = Account.MIN_TX_FEE_UALGOS;
            }
        }
        if (flatFee != null) {
            txn.fee = flatFee;
        }
        

        return txn;
    }

    /**
     * Set the group field. When present indicates that this transaction is part of a transaction group and the value is
     * the sha512/256 hash of the transactions in that group.
     * @param group The group.
     * @return This builder.
     */
    public T group(Digest group) {
        this.group = group;
        return (T) this;
    }

    /**
     * Set the group field as a raw byte array representation. When present indicates that this transaction is part of a
     * transaction group and the value is the sha512/256 hash of the transactions in that group.
     * @param group The group.
     * @return This builder.
     */
    public T group(byte[] group) {
        this.group = new Digest(group);
        return (T) this;
    }
    /**
     * Set the group field as a UTF-8 encoded string. When present indicates that this transaction is part of a
     * transaction group and the value is the sha512/256 hash of the transactions in that group.
     * @param group The group.
     * @return This builder.
     */
    public T groupUTF8(String group) {
        this.group = new Digest(group.getBytes(StandardCharsets.UTF_8));
        return (T) this;
    }

    /**
     * Set the group field as a base64 encoded string. When present indicates that this transaction is part of a
     * transaction group and the value is the sha512/256 hash of the transactions in that group.
     * @param group The group.
     * @return This builder.
     */
    public T groupB64(String group) {
        this.group = new Digest(Encoder.decodeFromBase64(group));
        return (T) this;
    }
}
