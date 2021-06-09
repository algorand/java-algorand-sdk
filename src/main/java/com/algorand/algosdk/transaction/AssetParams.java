package com.algorand.algosdk.transaction;

import com.algorand.algosdk.crypto.Address;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.codec.binary.Base64;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;

@JsonPropertyOrder(alphabetic=true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class AssetParams implements Serializable {
    // total asset issuance
    @JsonProperty("t")
    public BigInteger assetTotal = BigInteger.valueOf(0);

    // Decimals specifies the number of digits to display after the decimal
    // place when displaying this asset. A value of 0 represents an asset
    // that is not divisible, a value of 1 represents an asset divisible
    // into tenths, and so on. This value must be between 0 and 19
    // (inclusive).
    @JsonProperty("dc")
    public Integer assetDecimals = 0;

    // whether each account has their asset slot frozen for this asset by default
    @JsonProperty("df")
    public boolean assetDefaultFrozen = false;

    // a hint to the unit name of the asset
    @JsonProperty("un")
    public String assetUnitName = "";

    // the name of the asset
    @JsonProperty("an")
    public String assetName = "";

    // URL where more information about the asset can be retrieved
    @JsonProperty("au")
    public String url = "";

    // MetadataHash specifies a commitment to some unspecified asset
    // metadata. The format of this metadata is up to the application.
    @JsonProperty("am")
    public byte [] metadataHash;

    // the address which has the ability to reconfigure the asset
    @JsonProperty("m")
    public Address assetManager = new Address();

    // the asset reserve: assets owned by this address do not count against circulation
    @JsonProperty("r")
    public Address assetReserve = new Address();

    // the address which has the ability to freeze/unfreeze accounts holding this asset
    @JsonProperty("f")
    public Address assetFreeze = new Address();

    // the address which has the ability to issue clawbacks against asset-holding accounts
    @JsonProperty("c")
    public Address assetClawback = new Address();

    public AssetParams(
            BigInteger assetTotal,
            Integer assetDecimals,
            boolean defaultFrozen,
            String assetUnitName,
            String assetName,
            String url,
            byte [] metadataHash,
            Address manager,
            Address reserve,
            Address freeze,
            Address clawback) {
        if(assetTotal != null) this.assetTotal = assetTotal;
        if(assetDecimals != null) this.assetDecimals = assetDecimals;
        this.assetDefaultFrozen = defaultFrozen;
        if(manager != null) this.assetManager = manager;
        if(reserve != null) this.assetReserve = reserve;
        if(freeze != null) this.assetFreeze = freeze;
        if(clawback != null) this.assetClawback = clawback;

        if(assetDecimals != null) {
            if (assetDecimals < 0 || assetDecimals > 19) throw new RuntimeException("assetDecimals cannot be less than 0 or greater than 19");
            this.assetDecimals = assetDecimals;
        }

        if(assetUnitName != null) {
            if (assetUnitName.length() > 8) throw new RuntimeException("assetUnitName cannot be greater than 8 characters");
            this.assetUnitName = assetUnitName;
        }

        if(assetName != null) {
            if (assetName.length() > 32) throw new RuntimeException("assetName cannot be greater than 32 characters");
            this.assetName = assetName;
        }

        if(url != null) {
            if (url.length() > 96) throw new RuntimeException("asset url cannot be greater than 32 characters");
            this.url = url;
        }

        if(metadataHash != null) {
            if (metadataHash.length > 32) throw new RuntimeException("asset metadataHash cannot be greater than 32 bytes");
            if (!Base64.isBase64(metadataHash)) throw new RuntimeException("asset metadataHash '" + new String(metadataHash) + "' is not base64 encoded");
            this.metadataHash = metadataHash;
        }
    }

    public AssetParams() {

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssetParams that = (AssetParams) o;
        return assetTotal.equals(that.assetTotal) &&
            assetDecimals.equals(that.assetDecimals) &&
            (assetDefaultFrozen == that.assetDefaultFrozen) &&
            assetName.equals(that.assetName) &&
            assetUnitName.equals(that.assetUnitName) &&
            url.equals(that.url) &&
            Arrays.equals(metadataHash, that.metadataHash) &&
            assetManager.equals(that.assetManager) &&
            assetReserve.equals(that.assetReserve) &&
            assetFreeze.equals(that.assetFreeze) &&
            assetClawback.equals(that.assetClawback);
    }

    @JsonCreator
    private AssetParams(@JsonProperty("t") BigInteger assetTotal,
                        @JsonProperty("dc") Integer assetDecimals,
                        @JsonProperty("df") boolean assetDefaultFrozen,
                        @JsonProperty("un") String assetUnitName,
                        @JsonProperty("an") String assetName,
                        @JsonProperty("au") String url,
                        @JsonProperty("am") byte[] metadataHash,
                        @JsonProperty("m") byte[] assetManager,
                        @JsonProperty("r") byte[] assetReserve,
                        @JsonProperty("f") byte[] assetFreeze,
                        @JsonProperty("c") byte[] assetClawback) {
        this(assetTotal, assetDecimals, assetDefaultFrozen, assetUnitName, assetName, url, metadataHash, new Address(assetManager), new Address(assetReserve), new Address(assetFreeze), new Address(assetClawback));
    }
}
