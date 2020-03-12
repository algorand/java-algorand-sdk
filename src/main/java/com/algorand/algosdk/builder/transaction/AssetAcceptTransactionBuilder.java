package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.Transaction;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

/**
 * Build an asset accept transaction.
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

        return Transaction.createAssetTransferTransaction(
                acceptingAccount,
                acceptingAccount,
                new Address(),
                BigInteger.valueOf(0),
                flatFee,
                firstValid,
                lastValid,
                note,
                genesisID,
                genesisHash,
                assetIndex);
    }

    /**
     * Set the acceptingAccount.
     */
    public T acceptingAccount(Address acceptingAccount) {
        this.acceptingAccount = acceptingAccount;
        return (T) this;
    }

    /**
     * Set the acceptingAccount.
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
     * Set the acceptingAccount.
     */
    public T acceptingAccount(byte[] acceptingAccount) {
        this.acceptingAccount = new Address(acceptingAccount);
        return (T) this;
    }

    /**
     * Set the assetIndex.
     */
    public T assetIndex(BigInteger assetIndex) {
        this.assetIndex = assetIndex;
        return (T) this;
    }

    /**
     * Set the assetIndex.
     */
    public T assetIndex(Integer assetIndex) {
        if (assetIndex < 0) throw new IllegalArgumentException("assetIndex cannot be a negative value");
        this.assetIndex = BigInteger.valueOf(assetIndex);
        return (T) this;
    }

    /**
     * Set the assetIndex.
     */
    public T assetIndex(Long assetIndex) {
        if (assetIndex < 0) throw new IllegalArgumentException("assetIndex cannot be a negative value");
        this.assetIndex = BigInteger.valueOf(assetIndex);
        return (T) this;
    }
}
