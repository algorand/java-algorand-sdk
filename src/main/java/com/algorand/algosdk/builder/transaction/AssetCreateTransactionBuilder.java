package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.AssetParams;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * Build an asset create transaction, a specialized form of the AssetConfigurationTransaction with a null index.
 *
 * Required parameters:
 *     assetTotal
 *     assetDecimals
 *     defaultFrozen
 *     genesisHash
 *
 * Optional parameters:
 *     assetName
 *     assetUnitName
 *     url
 *     metadataHash
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
 */
@SuppressWarnings("unchecked")
public class AssetCreateTransactionBuilder<T extends AssetCreateTransactionBuilder<T>> extends TransactionBuilder<T> {
    protected BigInteger assetTotal = null;
    protected Integer assetDecimals = null;
    protected boolean defaultFrozen = false;
    protected String assetUnitName = null;
    protected String assetName = null;
    protected String url = null;
    protected byte[] metadataHash = null;
    protected Address manager = null;
    protected Address reserve = null;
    protected Address freeze = null;
    protected Address clawback = null;

    /**
     * Initialize a {@link AssetCreateTransactionBuilder}.
     */
    public static AssetCreateTransactionBuilder<?> Builder() {
        return new AssetCreateTransactionBuilder<>();
    }

    private AssetCreateTransactionBuilder() {
        super(Transaction.Type.AssetConfig);
    }

    protected AssetCreateTransactionBuilder(Transaction.Type type) {
        super(type);
    }

    @Override
    protected void applyTo(Transaction txn) {
        if (this.getClass() == AssetCreateTransactionBuilder.class) {
            Objects.requireNonNull(sender, "sender is required.");
            Objects.requireNonNull(firstValid, "firstValid is required.");
            Objects.requireNonNull(lastValid, "lastValid is required.");
            Objects.requireNonNull(genesisHash, "genesisHash is required.");
            Objects.requireNonNull(assetTotal, "assetTotal is required.");
            Objects.requireNonNull(assetDecimals, "assetDecimals is required.");
        }

        AssetParams params = new AssetParams(
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
                clawback);
        txn.assetParams = params;
    }

    /**
     * Set the assetTotal, the total number of the asset issued.
     * @param assetTotal The assetTotal.
     * @return this builder.
     */
    public T assetTotal(BigInteger assetTotal) {
        this.assetTotal = assetTotal;
        return (T) this;
    }

    /**
     * Set the assetTotal, the total number of the asset issued.
     * @param assetTotal The assetTotal.
     * @return this builder.
     */
    public T assetTotal(Integer assetTotal) {
        if (assetTotal < 0) throw new IllegalArgumentException("assetTotal cannot be a negative value");
        this.assetTotal = BigInteger.valueOf(assetTotal);
        return (T) this;
    }

    /**
     * Set the assetTotal, the total number of the asset issued.
     * @param assetTotal The assetTotal.
     * @return this builder.
     */
    public T assetTotal(Long assetTotal) {
        if (assetTotal < 0) throw new IllegalArgumentException("assetTotal cannot be a negative value");
        this.assetTotal = BigInteger.valueOf(assetTotal);
        return (T) this;
    }

    /**
     * assetDecimals specifies the number of digits to display after the decimal
     * place when displaying this asset. A value of 0 represents an asset
     * that is not divisible, a value of 1 represents an asset divisible
     * into tenths, and so on. This value must be between 0 and 19
     * (inclusive).
     * @param assetDecimals The assetDecimals.
     * @return this builder.
     */
    public T assetDecimals(int assetDecimals) {
        this.assetDecimals = assetDecimals;
        return (T) this;
    }

    /**
     * Set whether accounts have this asset frozen by default.
     * @param defaultFrozen The defaultFrozen value.
     * @return this builder.
     */
    public T defaultFrozen(boolean defaultFrozen) {
        this.defaultFrozen = defaultFrozen;
        return (T) this;
    }

    /**
     * Set an assetUnitName used to describe the name of a single unit of the asset.
     * This value must be between 0 and 8 characters (inclusive).
     * @param assetUnitName The assetUnitName.
     * @return this builder.
     */
    public T assetUnitName(String assetUnitName) {
        this.assetUnitName = assetUnitName;
        return (T) this;
    }

    /**
     * Set the assetName used to describe the asset. This value must be between 0 and 32 characters (inclusive).
     * @param assetName The assetName.
     * @return this builder.
     */
    public T assetName(String assetName) {
        this.assetName = assetName;
        return (T) this;
    }

    /**
     * Set url. This value must be between 0 and 96 characters (inclusive).
     * @param url The asset url.
     * @return this builder.
     */
    public T url(String url) {
        if (url.length() > 96) {
            throw new IllegalArgumentException("url must be between 0 and 96 characters (inclusive).");
        }
        this.url = url;
        return (T) this;
    }

    /**
     * Set the metadataHash field.
     * @param metadataHash The metadataHash.
     * @return this builder.
     */
    public T metadataHash(byte[] metadataHash) {
        this.metadataHash = metadataHash;
        return (T) this;
    }
    /**
     * Set the metadataHash field using a UTF-8 encoded string.
     * @param metadataHash The metadataHash.
     * @return this builder.
     */
    public T metadataHashUTF8(String metadataHash) {
        this.metadataHash = metadataHash.getBytes(StandardCharsets.UTF_8);
        return (T) this;
    }

    /**
     * Set the metadataHash field using a Base64 encoded string.
     * @param metadataHash The metadataHash.
     * @return this builder.
     */
    public T metadataHashB64(String metadataHash) {
        this.metadataHash = Encoder.decodeFromBase64(metadataHash);
        return (T) this;
    }


    /**
     * Set the manager account. The manager account can reconfigure the asset.
     * @param manager The manager account.
     * @return this builder.
     */
    public T manager(Address manager) {
        this.manager = manager;
        return (T) this;
    }

    /**
     * Set the manager account in the human-readable address format. The manager account can reconfigure the asset.
     * @param manager The manager account.
     * @return this builder.
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
     * Set the manager account in the raw 32 byte format. The manager account can reconfigure the asset.
     * @param manager The manager account.
     * @return this builder.
     */
    public T manager(byte[] manager) {
        this.manager = new Address(manager);
        return (T) this;
    }

    /**
     * Set the reserve account. The reserve account holds all assets which are considered non-minted.
     * @param reserve The reserve account.
     * @return this builder.
     */
    public T reserve(Address reserve) {
        this.reserve = reserve;
        return (T) this;
    }

    /**
     * Set the reserve account in the human-readable address format. The reserve account holds all assets which are considered non-minted.
     * @param reserve The reserve account.
     * @return this builder.
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
     * Set the reserve account in the raw 32 byte format. The reserve account holds all assets which are considered non-minted.
     * @param reserve The reserve account.
     * @return this builder.
     */
    public T reserve(byte[] reserve) {
        this.reserve = new Address(reserve);
        return (T) this;
    }

    /**
     * Set the freeze account. The account which can freeze or thaw holder accounts.
     * @param freeze The freeze account.
     * @return this builder.
     */
    public T freeze(Address freeze) {
        this.freeze = freeze;
        return (T) this;
    }

    /**
     * Set the freeze account in the human-readable address format. The account which can freeze or thaw holder accounts.
     * @param freeze The freeze account.
     * @return this builder.
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
     * Set the freeze account in the raw 32 byte format. The account which can freeze or thaw holder accounts.
     * @param freeze The freeze account.
     * @return this builder.
     */
    public T freeze(byte[] freeze) {
        this.freeze = new Address(freeze);
        return (T) this;
    }

    /**
     * Set the clawback account. This account can clawback assets from holder accounts.
     * @param clawback The clawback account.
     * @return this builder.
     */
    public T clawback(Address clawback) {
        this.clawback = clawback;
        return (T) this;
    }

    /**
     * Set the clawback account in the human-readable address format. This account can clawback assets from holder accounts.
     * @param clawback The clawback account.
     * @return this builder.
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
     * Set the clawback account in the raw 32 byte format. This account can clawback assets from holder accounts.
     * @param clawback The clawback account.
     * @return this builder.
     */
    public T clawback(byte[] clawback) {
        this.clawback = new Address(clawback);
        return (T) this;
    }
}
