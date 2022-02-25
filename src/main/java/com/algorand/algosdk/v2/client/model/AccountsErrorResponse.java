package com.algorand.algosdk.v2.client.model;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An error response for the /v2/accounts API endpoint, with optional information
 * about limits that were exceeded.
 */
public class AccountsErrorResponse extends PathResponse {

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

    @JsonProperty("max-results")
    public Long maxResults;

    @JsonProperty("message")
    public String message;

    @JsonProperty("total-apps-opted-in")
    public Long totalAppsOptedIn;

    @JsonProperty("total-assets-opted-in")
    public Long totalAssetsOptedIn;

    @JsonProperty("total-created-apps")
    public Long totalCreatedApps;

    @JsonProperty("total-created-assets")
    public Long totalCreatedAssets;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        AccountsErrorResponse other = (AccountsErrorResponse) o;
        if (!Objects.deepEquals(this.address, other.address)) return false;
        if (!Objects.deepEquals(this.maxResults, other.maxResults)) return false;
        if (!Objects.deepEquals(this.message, other.message)) return false;
        if (!Objects.deepEquals(this.totalAppsOptedIn, other.totalAppsOptedIn)) return false;
        if (!Objects.deepEquals(this.totalAssetsOptedIn, other.totalAssetsOptedIn)) return false;
        if (!Objects.deepEquals(this.totalCreatedApps, other.totalCreatedApps)) return false;
        if (!Objects.deepEquals(this.totalCreatedAssets, other.totalCreatedAssets)) return false;

        return true;
    }
}
