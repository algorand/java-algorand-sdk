package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Fields for an asset transfer transaction. 
 * Definition: 
 * data/transactions/asset.go : AssetTransferTxnFields 
 */
public class TransactionAssetTransfer extends PathResponse {

    /**
     * (aamt) Amount of asset to transfer. A zero amount transferred to self allocates 
     * that asset in the account's Assets map. 
     */
    @JsonProperty("amount")
    public java.math.BigInteger amount;

    /**
     * (xaid) ID of the asset being transferred. 
     */
    @JsonProperty("asset-id")
    public Long assetId;

    /**
     * Number of assets transfered to the close-to account as part of the transaction. 
     */
    @JsonProperty("close-amount")
    public java.math.BigInteger closeAmount;

    /**
     * (aclose) Indicates that the asset should be removed from the account's Assets 
     * map, and specifies where the remaining asset holdings should be transferred. 
     * It's always valid to transfer remaining asset holdings to the creator account. 
     */
    @JsonProperty("close-to")
    public String closeTo;

    /**
     * (arcv) Recipient address of the transfer. 
     */
    @JsonProperty("receiver")
    public String receiver;

    /**
     * (asnd) The effective sender during a clawback transactions. If this is not a 
     * zero value, the real transaction sender must be the Clawback address from the 
     * AssetParams. 
     */
    @JsonProperty("sender")
    public String sender;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        TransactionAssetTransfer other = (TransactionAssetTransfer) o;
        if (!Objects.deepEquals(this.amount, other.amount)) return false;
        if (!Objects.deepEquals(this.assetId, other.assetId)) return false;
        if (!Objects.deepEquals(this.closeAmount, other.closeAmount)) return false;
        if (!Objects.deepEquals(this.closeTo, other.closeTo)) return false;
        if (!Objects.deepEquals(this.receiver, other.receiver)) return false;
        if (!Objects.deepEquals(this.sender, other.sender)) return false;

        return true;
    }
}
