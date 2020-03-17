package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.transaction.Transaction;

import java.math.BigInteger;

/**
 * Build an asset configure transaction.
 *
 * Required parameters:
 *     assetIndex
 *     genesisHash
 *
 * Optional parameters:
 *     manager
 *     reserve
 *     freeze
 *     clawback
 *
 * Optional global parameters
 *     fee/flatFee
 *     note
 *     genesisID
 *     group
 *     lease
 *
 * You may only set addresses which are not zero for the existing asset.
 * Do not set assetUnitName, assetName, url, metadataHash, assetDecimals, assetTotal or defaultFrozen.
 */
public class AssetConfigureTransactionBuilder<T extends AssetConfigureTransactionBuilder<T>> extends AssetCreateTransactionBuilder<T> {
    protected BigInteger assetIndex = null;
    protected boolean strictEmptyAddressChecking = true;

    /**
     * Initialize a {@link AssetConfigureTransactionBuilder}.
     */
    public static AssetConfigureTransactionBuilder<?> Builder() {
        return new AssetConfigureTransactionBuilder<>();
    }

    @Override
    protected Transaction buildInternal() {
        if (this.assetUnitName != null) {
            throw new IllegalArgumentException("Must not set assetUnitName.");
        }
        if (this.assetName != null) {
            throw new IllegalArgumentException("Must not set assetName.");
        }
        if (this.url != null) {
            throw new IllegalArgumentException("Must not set url.");
        }
        if (this.metadataHash != null) {
            throw new IllegalArgumentException("Must not set metadataHash.");
        }
        if (this.assetDecimals != null) {
            throw new IllegalArgumentException("Must not set assetDecimals.");
        }
        if (this.assetTotal != null) {
            throw new IllegalArgumentException("Must not set assetTotal.");
        }
        if (this.defaultFrozen != false) {
            throw new IllegalArgumentException("Must not set defaultFrozen.");
        }

        return Transaction.createAssetConfigureTransaction(
                sender,
                fee,
                firstValid,
                lastValid,
                note,
                genesisID,
                genesisHash,
                assetIndex,
                manager,
                reserve,
                freeze,
                clawback,
                strictEmptyAddressChecking);
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
     * Whether to disallow empty admin accounts from being set. They cannot be reset to the correct address later
     * @param strictEmptyAddressChecking The strictEmptyAddressChecking value.
     * @return This builder.
     */
    public T strictEmptyAddressChecking(boolean strictEmptyAddressChecking) {
        this.strictEmptyAddressChecking = strictEmptyAddressChecking;
        return (T) this;
    }
}
