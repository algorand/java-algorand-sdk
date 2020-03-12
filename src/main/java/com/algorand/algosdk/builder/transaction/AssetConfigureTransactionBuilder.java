package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.transaction.Transaction;

import java.math.BigInteger;

/**
 * Build an asset configure transaction.
 */
public class AssetConfigureTransactionBuilder<T extends AssetConfigureTransactionBuilder<T>> extends AssetCreateTransactionBuilder<T> {
    protected BigInteger assetIndex = null;
    protected boolean strictEmptyAddressChecking = true;

    /**
     * Initialize a {@link AssetConfigureTransactionBuilder}.
     */
    public static AssetConfigureTransactionBuilder<?> Builder() {
        return new AssetConfigureTransactionBuilder<>();
    }

    @Override
    protected Transaction buildInternal() {
        return Transaction.createAssetConfigureTransaction(
                sender,
                fee,
                firstValid,
                lastValid,
                note,
                genesisID,
                genesisHash,
                assetIndex,
                manager,
                reserve,
                freeze,
                clawback,
                strictEmptyAddressChecking);
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

    /**
     * Whether to disallow empty admin accounts from being set. They cannot be reset to the correct address later
     */
    public T strictEmptyAddressChecking(boolean strictEmptyAddressChecking) {
        this.strictEmptyAddressChecking = strictEmptyAddressChecking;
        return (T) this;
    }
}
