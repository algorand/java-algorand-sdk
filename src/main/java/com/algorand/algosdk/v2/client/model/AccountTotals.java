package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Total Algos in the system grouped by account status
 */
public class AccountTotals extends PathResponse {

    /**
     * Amount of stake in non-participating accounts
     */
    @JsonProperty("not-participating")
    public Long notParticipating;

    /**
     * Amount of stake in offline accounts
     */
    @JsonProperty("offline")
    public Long offline;

    /**
     * Amount of stake in online accounts
     */
    @JsonProperty("online")
    public Long online;

    /**
     * Total number of algos received per reward unit since genesis
     */
    @JsonProperty("rewards-level")
    public Long rewardsLevel;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        AccountTotals other = (AccountTotals) o;
        if (!Objects.deepEquals(this.notParticipating, other.notParticipating)) return false;
        if (!Objects.deepEquals(this.offline, other.offline)) return false;
        if (!Objects.deepEquals(this.online, other.online)) return false;
        if (!Objects.deepEquals(this.rewardsLevel, other.rewardsLevel)) return false;

        return true;
    }
}
