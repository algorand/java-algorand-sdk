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
    private Address freezeTarget = null;
    private BigInteger assetFreezeID = null;
    private boolean freezeState = false;

    /**
     * Initialize a {@link AssetFreezeTransactionBuilder}.
     */
    public static AssetFreezeTransactionBuilder<?> Builder() {
        return new AssetFreezeTransactionBuilder<>();
    }

    public AssetFreezeTransactionBuilder() {
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
                assetFreezeID);
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
     * Set the assetFreezeID.
     */
    public T assetFreezeID(BigInteger assetFreezeID) {
        this.assetFreezeID = assetFreezeID;
        return (T) this;
    }

    /**
     * Set the assetFreezeID.
     */
    public T assetFreezeID(Integer assetFreezeID) {
        if (assetFreezeID < 0) throw new IllegalArgumentException("assetFreezeID cannot be a negative value");
        this.assetFreezeID = BigInteger.valueOf(assetFreezeID);
        return (T) this;
    }

    /**
     * Set the assetFreezeID.
     */
    public T assetFreezeID(Long assetFreezeID) {
        if (assetFreezeID < 0) throw new IllegalArgumentException("assetFreezeID cannot be a negative value");
        this.assetFreezeID = BigInteger.valueOf(assetFreezeID);
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
