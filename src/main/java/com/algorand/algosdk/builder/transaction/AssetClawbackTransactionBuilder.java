package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.Transaction;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * Build an asset clawback transaction. These transactions allow the asset clawback account
 * to take assets away from holders of the asset. This is a specialized form of an AssetConfig transaction which
 * only succeeds when the sender is equal the clawback account address.
 *
 * Required parameters:
 *     sender
 *     assetClawbackFrom
 *     assetReceiver
 *     assetAmount
 *     genesisHash
 *
 * Optional parameters:
 *     assetCloseTo
 *
 * Optional global parameters:
 *     fee/flatFee
 *     note
 *     genesisID
 *     group
 *     lease
 */
public class AssetClawbackTransactionBuilder<T extends AssetClawbackTransactionBuilder<T>> extends TransactionBuilder<T> {
    protected Address assetClawbackFrom = null;
    protected Address assetReceiver = null;
    protected Address assetCloseTo = null;
    protected BigInteger assetAmount = null;
    protected BigInteger assetIndex = null;

    /**
     * Initialize a {@link AssetClawbackTransactionBuilder}.
     */
    public static AssetClawbackTransactionBuilder<?> Builder() {
        return new AssetClawbackTransactionBuilder<>();
    }

    protected AssetClawbackTransactionBuilder() {
        super(Transaction.Type.AssetTransfer);
    }

    @Override
    protected Transaction buildInternal() {
        Objects.requireNonNull(sender, "sender is required.");
        Objects.requireNonNull(assetClawbackFrom, "assetClawbackFrom is required.");
        Objects.requireNonNull(assetReceiver, "assetReceiver is required.");
        Objects.requireNonNull(assetAmount, "assetAmount is required.");
        Objects.requireNonNull(firstValid, "firstValid is required.");
        Objects.requireNonNull(lastValid, "lastValid is required.");
        Objects.requireNonNull(genesisHash, "genesisHash is required.");


        return new Transaction(
                Transaction.Type.AssetTransfer,
                //header fields
                sender,
                fee,
                firstValid,
                lastValid,
                note,
                genesisID,
                genesisHash,
                null,
                null,
                null,
                // payment fields
                null,
                null,
                null,
                // keyreg fields
                null,
                null,
                null,
                null,
                // voteKeyDilution
                null,
                // asset creation and configuration
                null,
                null,
                // asset transfer fields
                assetIndex,
                assetAmount,
                assetClawbackFrom,
                assetReceiver,
                assetCloseTo,
                // asset freeze fields
                null,
                null,
                false); // default value which wont be included in the serialized object.
    }

    /**
     * Set the assetClawbackFrom account. This is the asset who will have assets removed from their account.
     * @param assetClawbackFrom The assetClawbackFrom account.
     * @return this builder.
     */
    public T assetClawbackFrom(Address assetClawbackFrom) {
        this.assetClawbackFrom = assetClawbackFrom;
        return (T) this;
    }

    /**
     * Set the assetClawbackFrom account. This is the asset who will have assets removed from their account.
     * @param assetClawbackFrom The assetClawbackFrom account.
     * @return this builder.
     */
    public T assetClawbackFrom(String assetClawbackFrom) {
        try {
            this.assetClawbackFrom = new Address(assetClawbackFrom);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        return (T) this;
    }

    /**
     * Set the assetClawbackFrom account. This is the asset who will have assets removed from their account.
     * @param assetClawbackFrom The assetClawbackFrom account.
     * @return this builder.
     */
    public T assetClawbackFrom(byte[] assetClawbackFrom) {
        this.assetClawbackFrom = new Address(assetClawbackFrom);
        return (T) this;
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
     * Set the assetAmount. The number of assets to clawback.
     * @param assetAmount The assetAmount.
     * @return This builder.
     */
    public T assetAmount(BigInteger assetAmount) {
        this.assetAmount = assetAmount;
        return (T) this;
    }

    /**
     * Set the assetAmount. The number of assets to clawback.
     * @param assetAmount The assetAmount.
     * @return This builder.
     */
    public T assetAmount(Integer assetAmount) {
        if (assetAmount < 0) throw new IllegalArgumentException("assetAmount cannot be a negative value");
        this.assetAmount = BigInteger.valueOf(assetAmount);
        return (T) this;
    }

    /**
     * Set the assetAmount. The number of assets to clawback.
     * @param assetAmount The assetAmount.
     * @return This builder.
     */
    public T assetAmount(Long assetAmount) {
        if (assetAmount < 0) throw new IllegalArgumentException("assetAmount cannot be a negative value");
        this.assetAmount = BigInteger.valueOf(assetAmount);
        return (T) this;
    }
}
