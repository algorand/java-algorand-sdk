package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.Transaction;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

/**
 * Build an asset transfer transaction for sending some asset from an asset holder to another user.
 *  The asset receiver must have marked itself as willing to accept the asset.
 */
public class AssetTransferTransactionBuilder<T extends AssetTransferTransactionBuilder<T>> extends TransactionBuilder<T> {
    Address assetSender = null;
    Address assetReceiver = null;
    Address assetCloseTo = null;
    BigInteger assetAmount = null;
    BigInteger assetIndex = null;

    /**
     * Initialize a {@link AssetTransferTransactionBuilder}.
     */
    public static AssetTransferTransactionBuilder<?> Builder() {
        return new AssetTransferTransactionBuilder<>();
    }

    public AssetTransferTransactionBuilder() {
        super(Transaction.Type.AssetTransfer);
    }

    @Override
    protected Transaction buildInternal() {
        if (sender != null) {
            throw new IllegalArgumentException("Do not use 'sender' for asset transfer transactions. Only use 'assetSender'");
        }

        return Transaction.createAssetTransferTransaction(
                assetSender,
                assetReceiver,
                assetCloseTo,
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
     * Set the assetSender.
     */
    public T assetSender(Address assetSender) {
        this.assetSender = assetSender;
        return (T) this;
    }

    /**
     * Set the assetSender.
     */
    public T assetSender(String assetSender) {
        try {
            this.assetSender = new Address(assetSender);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        return (T) this;
    }

    /**
     * Set the assetSender.
     */
    public T assetSender(byte[] assetSender) {
        this.assetSender = new Address(assetSender);
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
