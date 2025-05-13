package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * An operation against an application's global/local/box state.
 */
public class ApplicationStateOperation extends PathResponse {

    /**
     * For local state changes, the address of the account associated with the local
     * state.
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
     * Type of application state. Value `g` is   global state  , `l` is   local
     * state  , `b` is   boxes  .
     */
    @JsonProperty("app-state-type")
    public String appStateType;

    /**
     * The key (name) of the global/local/box state.
     */
    @JsonProperty("key")
    public void key(String base64Encoded) {
        this.key = Encoder.decodeFromBase64(base64Encoded);
    }
    public String key() {
        return Encoder.encodeToBase64(this.key);
    }
    public byte[] key;

    /**
     * Represents an AVM value.
     */
    @JsonProperty("new-value")
    public AvmValue newValue;

    /**
     * Operation type. Value `w` is   write  , `d` is   delete  .
     */
    @JsonProperty("operation")
    public String operation;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        ApplicationStateOperation other = (ApplicationStateOperation) o;
        if (!Objects.deepEquals(this.account, other.account)) return false;
        if (!Objects.deepEquals(this.appStateType, other.appStateType)) return false;
        if (!Objects.deepEquals(this.key, other.key)) return false;
        if (!Objects.deepEquals(this.newValue, other.newValue)) return false;
        if (!Objects.deepEquals(this.operation, other.operation)) return false;

        return true;
    }
}
