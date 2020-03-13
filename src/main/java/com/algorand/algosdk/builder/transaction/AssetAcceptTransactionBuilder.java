package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.Transaction;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

/**
 * Build an asset accept transaction, which is used to mark an acount as willing to accept an asset.
 */
public class AssetAcceptTransactionBuilder<T extends AssetAcceptTransactionBuilder<T>> extends TransactionBuilder<T> {
    protected Address acceptingAccount = null;
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
    protected Transaction buildInternal() {
        if (sender != null) {
            throw new IllegalArgumentException("Do not use 'sender' for asset transfer transactions. Only use 'assetSender'");
        }

        return Transaction.createAssetAcceptTransaction(
                acceptingAccount,
                fee,
                firstValid,
                lastValid,
                note,
                genesisID,
                genesisHash,
                assetIndex);
    }

    /**
     * Set the acceptingAccount.
     * @param acceptingAccount The acceptingAccount.
     * @return This builder.
     */
    public T acceptingAccount(Address acceptingAccount) {
        this.acceptingAccount = acceptingAccount;
        return (T) this;
    }

    /**
     * Set the acceptingAccount in the human-readable address format.
     * @param acceptingAccount The acceptingAccount.
     * @return This builder.
     */
    public T acceptingAccount(String acceptingAccount) {
        try {
            this.acceptingAccount = new Address(acceptingAccount);
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
        this.acceptingAccount = new Address(acceptingAccount);
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
