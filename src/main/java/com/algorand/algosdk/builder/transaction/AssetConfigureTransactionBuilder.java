package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.transaction.Transaction;

import java.math.BigInteger;

/**
 * Build an asset create transaction. This a specialized form of an AssetConfig Transaction with a null index.
 */
public class AssetConfigureTransactionBuilder<T extends AssetConfigureTransactionBuilder<T>> extends AssetCreateTransactionBuilder<T> {
    BigInteger index = null;
    boolean strictEmptyAddressChecking = true;

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
                index,
                manager,
                reserve,
                freeze,
                clawback,
                strictEmptyAddressChecking);
    }

    /**
     * Set the index.
     */
    public T index(BigInteger index) {
        this.index = index;
        return (T) this;
    }

    /**
     * Set the index.
     */
    public T index(Integer index) {
        if (index < 0) throw new IllegalArgumentException("index cannot be a negative value");
        this.index = BigInteger.valueOf(index);
        return (T) this;
    }

    /**
     * Set the index.
     */
    public T index(Long index) {
        if (index < 0) throw new IllegalArgumentException("index cannot be a negative value");
        this.index = BigInteger.valueOf(index);
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
