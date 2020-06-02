package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.transaction.Transaction.Type;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

/**
 * Build an asset freeze transaction.
 *
 * Required parameters:
 *     assetIndex
 *     freezeTarget
 *     freezeState
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
    protected void applyTo(Transaction txn) {
        if (freezeTarget != null) txn.freezeTarget = freezeTarget;
        if (assetIndex != null) txn.assetFreezeID = assetIndex;
        txn.freezeState = freezeState;
    }

    /**
     * Set the freezeTarget account. This is the account that will be frozen or thawed.
     * @param freezeTarget The freezeTarget account.
     * @return this builder.
     */
    public T freezeTarget(Address freezeTarget) {
        this.freezeTarget = freezeTarget;
        return (T) this;
    }

    /**
     * Set the freezeTarget account in the human-readable address format. This is the account that will be frozen or thawed.
     * @param freezeTarget The freezeTarget account.
     * @return this builder.
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
     * Set the freezeTarget account in the raw 32 byte format. This is the account that will be frozen or thawed.
     * @param freezeTarget The freezeTarget account.
     * @return this builder.
     */
    public T freezeTarget(byte[] freezeTarget) {
        this.freezeTarget = new Address(freezeTarget);
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

    /**
     * Set the freezeState of the freezeTarget account.
     * @param freezeState The freezeState.
     * @return This builder.
     */
    public T freezeState(boolean freezeState) {
        this.freezeState = freezeState;
        return (T) this;
    }
}
