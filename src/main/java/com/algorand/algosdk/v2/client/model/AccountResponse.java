package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class AccountResponse extends PathResponse {

    /**
     * Account information at a given round.
     * Definition:
     * data/basics/userBalance.go : AccountData
     *
     */
    @JsonProperty("account")
    public Account account;

    /**
     * Round at which the results were computed.
     */
    @JsonProperty("current-round")
    public Long currentRound;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        AccountResponse other = (AccountResponse) o;
        if (!Objects.deepEquals(this.account, other.account)) return false;
        if (!Objects.deepEquals(this.currentRound, other.currentRound)) return false;

        return true;
    }
}
