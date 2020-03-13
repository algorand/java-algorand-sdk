package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.transaction.Transaction;

import java.math.BigInteger;

/**
 * Build an asset destroy transaction.
 */
public class AssetDestroyTransactionBuilder<T extends AssetDestroyTransactionBuilder<T>> extends TransactionBuilder<T> {
    protected BigInteger assetIndex = null;

    /**
     * Initialize a {@link AssetDestroyTransactionBuilder}.
     */
    public static AssetDestroyTransactionBuilder<?> Builder() {
        return new AssetDestroyTransactionBuilder<>();
    }

    protected AssetDestroyTransactionBuilder() {
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
                assetIndex);
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
