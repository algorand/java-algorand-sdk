package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.Transaction;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

/**
 * Build an asset freeze transaction.
 */
public class AssetFreezeTransactionBuilder<T extends AssetFreezeTransactionBuilder<T>> extends TransactionBuilder<T> {
    // asset freeze fields
    protected Address freezeTarget = null;
    protected BigInteger assetIndex = null;
    protected boolean freezeState = false;

    /**
     * Initialize a {@link AssetFreezeTransactionBuilder}.
     */
    public static AssetFreezeTransactionBuilder<?> Builder() {
        return new AssetFreezeTransactionBuilder<>();
    }

    protected AssetFreezeTransactionBuilder() {
        super(Transaction.Type.AssetFreeze);
    }

    @Override
    protected Transaction buildInternal() {
        return Transaction.createAssetFreezeTransaction(
                sender,
                freezeTarget,
                freezeState,
                fee,
                firstValid,
                lastValid,
                note,
                genesisHash,
                assetIndex);
    }

    /**
     * Set the freezeTarget.
     */
    public T freezeTarget(Address freezeTarget) {
        this.freezeTarget = freezeTarget;
        return (T) this;
    }

    /**
     * Set the freezeTarget.
     */
    public T freezeTarget(String freezeTarget) {
        try {
            this.freezeTarget = new Address(freezeTarget);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        return (T) this;
    }

    /**
     * Set the freezeTarget.
     */
    public T freezeTarget(byte[] freezeTarget) {
        this.freezeTarget = new Address(freezeTarget);
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

    /**
     * Set the freezeState.
     */
    public T freezeState(boolean freezeState) {
        this.freezeState = freezeState;
        return (T) this;
    }
}
