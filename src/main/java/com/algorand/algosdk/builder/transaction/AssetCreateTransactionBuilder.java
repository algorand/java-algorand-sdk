package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

/**
 * Build an asset create transaction, a specialized form of the AssetConfigurationTransaction with a null index.
 */
public class AssetCreateTransactionBuilder<T extends AssetCreateTransactionBuilder<T>> extends TransactionBuilder<T> {
    BigInteger assetTotal = null;
    Integer assetDecimals = null;
    boolean defaultFrozen = false;
    String assetUnitName = null;
    String assetName = null;
    String url = null;
    byte[] metadataHash = null;
    Address manager = null;
    Address reserve = null;
    Address freeze = null;
    Address clawback = null;

    /**
     * Initialize a {@link AssetCreateTransactionBuilder}.
     */
    public static AssetCreateTransactionBuilder<?> Builder() {
        return new AssetCreateTransactionBuilder<>();
    }

    public AssetCreateTransactionBuilder() {
        super(Transaction.Type.AssetConfig);
    }

    @Override
    protected Transaction buildInternal() {
        return Transaction.createAssetCreateTransaction(
                sender,
                fee,
                firstValid,
                lastValid,
                note,
                genesisID,
                genesisHash,
                assetTotal,
                assetDecimals,
                defaultFrozen,
                assetUnitName,
                assetName,
                url,
                metadataHash,
                manager,
                reserve,
                freeze,
                clawback
        );
    }

    /**
     * Set the assetTotal.
     */
    public T assetTotal(BigInteger assetTotal) {
        this.assetTotal = assetTotal;
        return (T) this;
    }

    /**
     * Set the assetTotal.
     */
    public T assetTotal(Integer assetTotal) {
        if (assetTotal < 0) throw new IllegalArgumentException("assetTotal cannot be a negative value");
        this.assetTotal = BigInteger.valueOf(assetTotal);
        return (T) this;
    }

    /**
     * Set the assetTotal.
     */
    public T assetTotal(Long assetTotal) {
        if (assetTotal < 0) throw new IllegalArgumentException("assetTotal cannot be a negative value");
        this.assetTotal = BigInteger.valueOf(assetTotal);
        return (T) this;
    }

    /**
     * Set the number of decimals.
     */
    public T assetDecimals(int assetDecimals) {
        this.assetDecimals = assetDecimals;
        return (T) this;
    }

    /**
     * Set defaultFrozen.
     */
    public T defaultFrozen(boolean defaultFrozen) {
        this.defaultFrozen = defaultFrozen;
        return (T) this;
    }

    /**
     * Set assetUnitName.
     */
    public T assetUnitName(String assetUnitName) {
        this.assetUnitName = assetUnitName;
        return (T) this;
    }

    /**
     * Set assetName.
     */
    public T assetName(String assetName) {
        this.assetName = assetName;
        return (T) this;
    }

    /**
     * Set url.
     */
    public T url(String url) {
        this.url = url;
        return (T) this;
    }

    /**
     * Set the metadataHash field.
     * @param metadataHash
     */
    public T metadataHash(byte[] metadataHash) {
        this.metadataHash = metadataHash;
        return (T) this;
    }
    /**
     * Set the metadataHash field using a UTF-8 encoded string.
     * @param metadataHash
     */
    public T metadataHashUTF8(String metadataHash) {
        this.metadataHash = metadataHash.getBytes(StandardCharsets.UTF_8);
        return (T) this;
    }

    /**
     * Set the metadataHash field using a Base64 encoded string.
     * @param metadataHash
     */
    public T metadataHashB64(String metadataHash) {
        this.metadataHash = Encoder.decodeFromBase64(metadataHash);
        return (T) this;
    }


    /**
     * Set the manager.
     * @param manager
     */
    public T manager(Address manager) {
        this.manager = manager;
        return (T) this;
    }

    /**
     * Set the manager.
     * @param manager
     */
    public T manager(String manager) {
        try {
            this.manager = new Address(manager);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        return (T) this;
    }

    /**
     * Set the manager.
     * @param manager
     */
    public T manager(byte[] manager) {
        this.manager = new Address(manager);
        return (T) this;
    }

    /**
     * Set the reserve.
     * @param reserve
     */
    public T reserve(Address reserve) {
        this.reserve = reserve;
        return (T) this;
    }

    /**
     * Set the reserve.
     * @param reserve
     */
    public T reserve(String reserve) {
        try {
            this.reserve = new Address(reserve);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        return (T) this;
    }

    /**
     * Set the reserve.
     * @param reserve
     */
    public T reserve(byte[] reserve) {
        this.reserve = new Address(reserve);
        return (T) this;
    }

    /**
     * Set the freeze.
     * @param freeze
     */
    public T freeze(Address freeze) {
        this.freeze = freeze;
        return (T) this;
    }

    /**
     * Set the freeze.
     * @param freeze
     */
    public T freeze(String freeze) {
        try {
            this.freeze = new Address(freeze);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        return (T) this;
    }

    /**
     * Set the freeze.
     * @param freeze
     */
    public T freeze(byte[] freeze) {
        this.freeze = new Address(freeze);
        return (T) this;
    }

    /**
     * Set the clawback.
     * @param clawback
     */
    public T clawback(Address clawback) {
        this.clawback = clawback;
        return (T) this;
    }

    /**
     * Set the clawback.
     * @param clawback
     */
    public T clawback(String clawback) {
        try {
            this.clawback = new Address(clawback);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        return (T) this;
    }

    /**
     * Set the clawback.
     * @param clawback
     */
    public T clawback(byte[] clawback) {
        this.clawback = new Address(clawback);
        return (T) this;
    }
}
