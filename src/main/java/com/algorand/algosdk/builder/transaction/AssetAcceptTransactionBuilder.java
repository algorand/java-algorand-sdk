package com.algorand.algosdk.builder.transaction;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.transaction.Transaction.Type;

/**
 * Build an asset accept transaction, which is used to mark an acount as willing to accept an asset.
 *
 * Required parameters:
 *     acceptingAccount
 *     assetIndex
 *     firstValid
 *     genesisHash
 *
 * Optional global parameters:
 *     fee/flatFee
 *     note
 *     genesisID
 *     lease
 *     group
 *
 *  Note: The acceptingAccount setters map to the 'sender' field internally. Using both will override each other.
 */
@SuppressWarnings("unchecked")
public class AssetAcceptTransactionBuilder<T extends AssetAcceptTransactionBuilder<T>> extends TransactionBuilder<T> {
    protected BigInteger assetIndex = null;

    /**
     * Initialize a {@link AssetAcceptTransactionBuilder}.
     */
    public static AssetAcceptTransactionBuilder<?> Builder() {
        return new AssetAcceptTransactionBuilder<>();
    }

    protected AssetAcceptTransactionBuilder() {
        super(Transaction.Type.AssetTransfer);
    }

    @Override
    protected void buildInternal() {
        if (this.txn == null) {
            txn = new Transaction();
            txn.type = Type.AssetTransfer;
        }

        if (assetIndex != null) txn.xferAsset = assetIndex; 
        if (sender != null) txn.assetReceiver = sender;
        txn.amount = BigInteger.valueOf(0);
    }

    /**
     * Set the acceptingAccount.
     * @param acceptingAccount The acceptingAccount.
     * @return This builder.
     */
    public T acceptingAccount(Address acceptingAccount) {
        this.sender = acceptingAccount;
        return (T) this;
    }

    /**
     * Set the acceptingAccount in the human-readable address format.
     * @param acceptingAccount The acceptingAccount.
     * @return This builder.
     */
    public T acceptingAccount(String acceptingAccount) {
        try {
            this.sender = new Address(acceptingAccount);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        return (T) this;
    }

    /**
     * Set the acceptingAccount in the raw 32 byte format.
     * @param acceptingAccount The acceptingAccount.
     * @return This builder.
     */
    public T acceptingAccount(byte[] acceptingAccount) {
        this.sender = new Address(acceptingAccount);
        return (T) this;
    }

    /**
     * Set the assetIndex.
     * @param assetIndex The assetIndex.
     * @return This builder.
     */
    public T assetIndex(BigInteger assetIndex) {
        this.assetIndex = assetIndex;
        return (T) this;
    }

    /**
     * Set the assetIndex.
     * @param assetIndex The assetIndex.
     * @return This builder.
     */
    public T assetIndex(Integer assetIndex) {
        if (assetIndex < 0) throw new IllegalArgumentException("assetIndex cannot be a negative value");
        this.assetIndex = BigInteger.valueOf(assetIndex);
        return (T) this;
    }

    /**
     * Set the assetIndex.
     * @param assetIndex The assetIndex.
     * @return This builder.
     */
    public T assetIndex(Long assetIndex) {
        if (assetIndex < 0) throw new IllegalArgumentException("assetIndex cannot be a negative value");
        this.assetIndex = BigInteger.valueOf(assetIndex);
        return (T) this;
    }
}
