package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * References an asset held by an account.
 */
public class AssetHoldingReference extends PathResponse {

    /**
     * Address of the account holding the asset.
     */
    @JsonProperty("account")
    public void account(String account) throws NoSuchAlgorithmException {
        this.account = new Address(account);
    }
    @JsonProperty("account")
    public String account() throws NoSuchAlgorithmException {
        if (this.account != null) {
            return this.account.encodeAsString();
        } else {
            return null;
        }
    }
    public Address account;

    /**
     * Asset ID of the holding.
     */
    @JsonProperty("asset")
    public java.math.BigInteger asset;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        AssetHoldingReference other = (AssetHoldingReference) o;
        if (!Objects.deepEquals(this.account, other.account)) return false;
        if (!Objects.deepEquals(this.asset, other.asset)) return false;

        return true;
    }
}
