package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.transaction.Transaction.Type;

import java.math.BigInteger;

/**
 * Build an asset destroy transaction.
 *
 * Required parameters:
 *     assetIndex
 *     genesisHash
 *
 * Optional global parameters
 *     fee/flatFee
 *     note
 *     genesisID
 *     group
 *     lease
 */
@SuppressWarnings("unchecked")
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
    protected void applyTo(Transaction txn) {
        if (this.getClass() == AssetDestroyTransactionBuilder.class) {
            txn.type = Type.AssetConfig;
        }
        if (assetIndex != null) {
            txn.assetIndex = assetIndex;
        }
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
