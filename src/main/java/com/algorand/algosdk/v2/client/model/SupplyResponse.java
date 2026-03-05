package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Supply represents the current supply of MicroAlgos in the system.
 */
public class SupplyResponse extends PathResponse {

    /**
     * Round
     */
    @JsonProperty("current_round")
    public Long current_round;

    /**
     * Total stake held by accounts with status Online at current_round, including
     * those whose participation keys have expired but have not yet been marked
     * offline.
     */
    @JsonProperty("online-money")
    public Long onlineMoney;

    /**
     * Online stake used by agreement to vote for current_round, excluding accounts
     * whose participation keys have expired.
     */
    @JsonProperty("online-stake")
    public Long onlineStake;

    /**
     * TotalMoney
     */
    @JsonProperty("total-money")
    public Long totalMoney;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        SupplyResponse other = (SupplyResponse) o;
        if (!Objects.deepEquals(this.current_round, other.current_round)) return false;
        if (!Objects.deepEquals(this.onlineMoney, other.onlineMoney)) return false;
        if (!Objects.deepEquals(this.onlineStake, other.onlineStake)) return false;
        if (!Objects.deepEquals(this.totalMoney, other.totalMoney)) return false;

        return true;
    }
}
