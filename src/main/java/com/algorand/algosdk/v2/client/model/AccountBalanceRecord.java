package com.algorand.algosdk.v2.client.model;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Account and its address
 */
public class AccountBalanceRecord extends PathResponse {

    /**
     * Updated account data.
     */
    @JsonProperty("account-data")
    public Account accountData;

    /**
     * Address of the updated account.
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

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        AccountBalanceRecord other = (AccountBalanceRecord) o;
        if (!Objects.deepEquals(this.accountData, other.accountData)) return false;
        if (!Objects.deepEquals(this.address, other.address)) return false;

        return true;
    }
}
