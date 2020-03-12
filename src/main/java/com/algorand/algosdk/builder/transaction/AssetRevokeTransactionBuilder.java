package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.Transaction;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

/**
 * Build an asset revoke transaction. Also known as "clawback", these transactions allow the asset clawback account
 * to take assets away from holders of the asset. This is a specialized form of an AssetConfig transaction which
 * only succeeds when the sender is equal the clawback account address.
 */
public class AssetRevokeTransactionBuilder<T extends AssetRevokeTransactionBuilder<T>> extends TransactionBuilder<T> {
    Address assetRevokeFrom = null;
    Address assetReceiver = null;
    Address assetCloseTo = null;
    BigInteger assetAmount = null;
    BigInteger assetIndex = null;

    /**
     * Initialize a {@link AssetRevokeTransactionBuilder}.
     */
    public static AssetRevokeTransactionBuilder<?> Builder() {
        return new AssetRevokeTransactionBuilder<>();
    }

    public AssetRevokeTransactionBuilder() {
        super(Transaction.Type.AssetTransfer);
    }

    @Override
    protected Transaction buildInternal() {
        return Transaction.createAssetRevokeTransaction(
                sender,
                assetRevokeFrom,
                assetReceiver,
                assetAmount,
                fee,
                firstValid,
                lastValid,
                note,
                genesisID,
                genesisHash,
                assetIndex);
    }

    /**
     * Set the assetRevokeFrom.
     */
    public T assetRevokeFrom(Address assetRevokeFrom) {
        this.assetRevokeFrom = assetRevokeFrom;
        return (T) this;
    }

    /**
     * Set the assetRevokeFrom.
     */
    public T assetRevokeFrom(String assetRevokeFrom) {
        try {
            this.assetRevokeFrom = new Address(assetRevokeFrom);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        return (T) this;
    }

    /**
     * Set the assetRevokeFrom.
     */
    public T assetRevokeFrom(byte[] assetRevokeFrom) {
        this.assetRevokeFrom = new Address(assetRevokeFrom);
        return (T) this;
    }

    /**
     * Set the assetReceiver.
     */
    public T assetReceiver(Address assetReceiver) {
        this.assetReceiver = assetReceiver;
        return (T) this;
    }

    /**
     * Set the assetReceiver.
     */
    public T assetReceiver(String assetReceiver) {
        try {
            this.assetReceiver = new Address(assetReceiver);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        return (T) this;
    }

    /**
     * Set the assetReceiver.
     */
    public T assetReceiver(byte[] assetReceiver) {
        this.assetReceiver = new Address(assetReceiver);
        return (T) this;
    }

    /**
     * Set the assetCloseTo.
     */
    public T assetCloseTo(Address assetCloseTo) {
        this.assetCloseTo = assetCloseTo;
        return (T) this;
    }

    /**
     * Set the assetCloseTo.
     */
    public T assetCloseTo(String assetCloseTo) {
        try {
            this.assetCloseTo = new Address(assetCloseTo);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        return (T) this;
    }

    /**
     * Set the assetCloseTo.
     */
    public T assetCloseTo(byte[] assetCloseTo) {
        this.assetCloseTo = new Address(assetCloseTo);
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
     * Set the assetAmount.
     */
    public T assetAmount(BigInteger assetAmount) {
        this.assetAmount = assetAmount;
        return (T) this;
    }

    /**
     * Set the assetAmount.
     */
    public T assetAmount(Integer assetAmount) {
        if (assetAmount < 0) throw new IllegalArgumentException("assetAmount cannot be a negative value");
        this.assetAmount = BigInteger.valueOf(assetAmount);
        return (T) this;
    }

    /**
     * Set the assetAmount.
     */
    public T assetAmount(Long assetAmount) {
        if (assetAmount < 0) throw new IllegalArgumentException("assetAmount cannot be a negative value");
        this.assetAmount = BigInteger.valueOf(assetAmount);
        return (T) this;
    }
}
