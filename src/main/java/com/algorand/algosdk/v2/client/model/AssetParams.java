package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AssetParams specifies the parameters for an asset.
 * (apar) when part of an AssetConfig transaction.
 * Definition:
 * data/transactions/asset.go : AssetParams
 */
public class AssetParams extends PathResponse {

    /**
     * (c) Address of account used to clawback holdings of this asset. If empty,
     * clawback is not permitted.
     */
    @JsonProperty("clawback")
    public String clawback;

    /**
     * The address that created this asset. This is the address where the parameters
     * for this asset can be found, and also the address where unwanted asset units can
     * be sent in the worst case.
     */
    @JsonProperty("creator")
    public String creator;

    /**
     * (dc) The number of digits to use after the decimal point when displaying this
     * asset. If 0, the asset is not divisible. If 1, the base unit of the asset is in
     * tenths. If 2, the base unit of the asset is in hundredths, and so on. This value
     * must be between 0 and 19 (inclusive).
     */
    @JsonProperty("decimals")
    public Long decimals;

    /**
     * (df) Whether holdings of this asset are frozen by default.
     */
    @JsonProperty("default-frozen")
    public Boolean defaultFrozen;

    /**
     * (f) Address of account used to freeze holdings of this asset. If empty, freezing
     * is not permitted.
     */
    @JsonProperty("freeze")
    public String freeze;

    /**
     * (m) Address of account used to manage the keys of this asset and to destroy it.
     */
    @JsonProperty("manager")
    public String manager;

    /**
     * (am) A commitment to some unspecified asset metadata. The format of this
     * metadata is up to the application.
     */
    @JsonProperty("metadata-hash")
    public void metadataHash(String base64Encoded) {
        this.metadataHash = Encoder.decodeFromBase64(base64Encoded);
    }
    @JsonProperty("metadata-hash")
    public String metadataHash() {
        return Encoder.encodeToBase64(this.metadataHash);
    }
    public byte[] metadataHash;

    /**
     * (an) Name of this asset, as supplied by the creator.
     */
    @JsonProperty("name")
    public String name;

    /**
     * (r) Address of account holding reserve (non-minted) units of this asset.
     */
    @JsonProperty("reserve")
    public String reserve;

    /**
     * (t) The total number of units of this asset.
     */
    @JsonProperty("total")
    public java.math.BigInteger total;

    /**
     * (un) Name of a unit of this asset, as supplied by the creator.
     */
    @JsonProperty("unit-name")
    public String unitName;

    /**
     * (au) URL where more information about the asset can be retrieved.
     */
    @JsonProperty("url")
    public String url;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        AssetParams other = (AssetParams) o;
        if (!Objects.deepEquals(this.clawback, other.clawback)) return false;
        if (!Objects.deepEquals(this.creator, other.creator)) return false;
        if (!Objects.deepEquals(this.decimals, other.decimals)) return false;
        if (!Objects.deepEquals(this.defaultFrozen, other.defaultFrozen)) return false;
        if (!Objects.deepEquals(this.freeze, other.freeze)) return false;
        if (!Objects.deepEquals(this.manager, other.manager)) return false;
        if (!Objects.deepEquals(this.metadataHash, other.metadataHash)) return false;
        if (!Objects.deepEquals(this.name, other.name)) return false;
        if (!Objects.deepEquals(this.reserve, other.reserve)) return false;
        if (!Objects.deepEquals(this.total, other.total)) return false;
        if (!Objects.deepEquals(this.unitName, other.unitName)) return false;
        if (!Objects.deepEquals(this.url, other.url)) return false;

        return true;
    }
}
