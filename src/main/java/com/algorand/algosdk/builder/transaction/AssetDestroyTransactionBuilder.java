package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.transaction.Transaction;

import java.math.BigInteger;

/**
 * Build an asset destroy transaction.
 */
public class AssetDestroyTransactionBuilder<T extends AssetDestroyTransactionBuilder<T>> extends TransactionBuilder<T> {
    private BigInteger index = null;

    /**
     * Initialize a {@link AssetDestroyTransactionBuilder}.
     */
    public static AssetDestroyTransactionBuilder<?> Builder() {
        return new AssetDestroyTransactionBuilder<>();
    }

    public AssetDestroyTransactionBuilder() {
        super(Transaction.Type.AssetConfig);
    }

    @Override
    protected Transaction buildInternal() {
        return Transaction.createAssetDestroyTransaction(
                sender,
                fee,
                firstValid,
                lastValid,
                note,
                genesisHash,
                index);
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
}
