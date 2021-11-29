package com.algorand.algosdk.v2.client.model;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Fields for an asset freeze transaction.
 * Definition:
 * data/transactions/asset.go : AssetFreezeTxnFields
 */
public class TransactionAssetFreeze extends PathResponse {

    /**
     * (fadd) Address of the account whose asset is being frozen or thawed.
     */
    @JsonProperty("address")
    public void address(String address) throws NoSuchAlgorithmException {
        this.address = new Address(address);
    }
    @JsonProperty("address")
    public String address() throws NoSuchAlgorithmException {
        if (this.address != null) {
            return this.address.encodeAsString();
        } else {
            return null;
        }
    }
    public Address address;

    /**
     * (faid) ID of the asset being frozen or thawed.
     */
    @JsonProperty("asset-id")
    public Long assetId;

    /**
     * (afrz) The new freeze status.
     */
    @JsonProperty("new-freeze-status")
    public Boolean newFreezeStatus;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        TransactionAssetFreeze other = (TransactionAssetFreeze) o;
        if (!Objects.deepEquals(this.address, other.address)) return false;
        if (!Objects.deepEquals(this.assetId, other.assetId)) return false;
        if (!Objects.deepEquals(this.newFreezeStatus, other.newFreezeStatus)) return false;

        return true;
    }
}
