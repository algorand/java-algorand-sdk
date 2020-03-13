package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.Transaction;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

/**
 * Build an asset transfer transaction for sending some asset from an asset holder to another user.
 * The asset receiver must have marked itself as willing to accept the asset.
 */
public class AssetTransferTransactionBuilder<T extends AssetTransferTransactionBuilder<T>> extends TransactionBuilder<T> {
    protected Address assetReceiver = null;
    protected Address assetCloseTo = null;
    protected BigInteger assetAmount = null;
    protected BigInteger assetIndex = null;

    /**
     * Initialize a {@link AssetTransferTransactionBuilder}.
     */
    public static AssetTransferTransactionBuilder<?> Builder() {
        return new AssetTransferTransactionBuilder<>();
    }

    protected AssetTransferTransactionBuilder() {
        super(Transaction.Type.AssetTransfer);
    }

    @Override
    protected Transaction buildInternal() {
        return Transaction.createAssetTransferTransaction(
                sender,
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
     * Set the assetReceiver account. This is the account who will receive the funds.
     * @param assetReceiver The assetReceiver account.
     * @return this builder.
     */
    public T assetReceiver(Address assetReceiver) {
        this.assetReceiver = assetReceiver;
        return (T) this;
    }

    /**
     * Set the assetReceiver account in the human-readable address format. This is the account who will receive the funds.
     * @param assetReceiver The assetReceiver account.
     * @return this builder.
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
     * Set the assetReceiver account in the raw 32 byte format. This is the account who will receive the funds.
     * @param assetReceiver The assetReceiver account.
     * @return this builder.
     */
    public T assetReceiver(byte[] assetReceiver) {
        this.assetReceiver = new Address(assetReceiver);
        return (T) this;
    }

    /**
     * Set the assetCloseTo account. If set this account will recieve any remaining balance after the assetAmount has been removed from the balance.
     * @param assetCloseTo The assetCloseTo account.
     * @return this builder.
     */
    public T assetCloseTo(Address assetCloseTo) {
        this.assetCloseTo = assetCloseTo;
        return (T) this;
    }

    /**
     * Set the assetCloseTo account in the human-readable address format. If set this account will recieve any remaining balance after the assetAmount has been removed from the balance.
     * @param assetCloseTo The assetCloseTo account.
     * @return this builder.
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
     * Set the assetCloseTo account in the raw 32 byte format. If set this account will recieve any remaining balance after the assetAmount has been removed from the balance.
     * @param assetCloseTo The assetCloseTo account.
     * @return this builder.
     */
    public T assetCloseTo(byte[] assetCloseTo) {
        this.assetCloseTo = new Address(assetCloseTo);
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
     * Set the assetAmount. The number of assets to transfer from sender to assetReceiver.
     * @param assetAmount The assetAmount.
     * @return This builder.
     */
    public T assetAmount(BigInteger assetAmount) {
        this.assetAmount = assetAmount;
        return (T) this;
    }

    /**
     * Set the assetAmount. The number of assets to transfer from sender to assetReceiver.
     * @param assetAmount The assetAmount.
     * @return This builder.
     */
    public T assetAmount(Integer assetAmount) {
        if (assetAmount < 0) throw new IllegalArgumentException("assetAmount cannot be a negative value");
        this.assetAmount = BigInteger.valueOf(assetAmount);
        return (T) this;
    }

    /**
     * Set the assetAmount. The number of assets to transfer from sender to assetReceiver.
     * @param assetAmount The assetAmount.
     * @return This builder.
     */
    public T assetAmount(Long assetAmount) {
        if (assetAmount < 0) throw new IllegalArgumentException("assetAmount cannot be a negative value");
        this.assetAmount = BigInteger.valueOf(assetAmount);
        return (T) this;
    }
}
