package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * References an account's local state for an application.
 */
public class ApplicationLocalReference extends PathResponse {

    /**
     * Address of the account with the local state.
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
     * Application ID of the local state application.
     */
    @JsonProperty("app")
    public java.math.BigInteger app;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        ApplicationLocalReference other = (ApplicationLocalReference) o;
        if (!Objects.deepEquals(this.account, other.account)) return false;
        if (!Objects.deepEquals(this.app, other.app)) return false;

        return true;
    }
}
