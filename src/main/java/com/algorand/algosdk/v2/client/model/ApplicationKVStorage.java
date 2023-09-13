package com.algorand.algosdk.v2.client.model;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An application's global/local/box state.
 */
public class ApplicationKVStorage extends PathResponse {

    /**
     * The address of the account associated with the local state.
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
     * Key-Value pairs representing application states.
     */
    @JsonProperty("kvs")
    public List<AvmKeyValue> kvs = new ArrayList<AvmKeyValue>();

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        ApplicationKVStorage other = (ApplicationKVStorage) o;
        if (!Objects.deepEquals(this.account, other.account)) return false;
        if (!Objects.deepEquals(this.kvs, other.kvs)) return false;

        return true;
    }
}
