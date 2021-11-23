package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.*;

/**
 * Fields relating to voting for a protocol upgrade.
 */
public class BlockUpgradeVote extends PathResponse {

    /**
     * (upgradeyes) Indicates a yes vote for the current proposal.
     */
    @JsonProperty("upgrade-approve")
    public Boolean upgradeApprove;

    /**
     * (upgradedelay) Indicates the time between acceptance and execution.
     */
    @JsonProperty("upgrade-delay")
    public Long upgradeDelay;

    /**
     * (upgradeprop) Indicates a proposed upgrade.
     */
    @JsonProperty("upgrade-propose")
    public String upgradePropose;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        BlockUpgradeVote other = (BlockUpgradeVote) o;
        if (!Objects.deepEquals(this.upgradeApprove, other.upgradeApprove)) return false;
        if (!Objects.deepEquals(this.upgradeDelay, other.upgradeDelay)) return false;
        if (!Objects.deepEquals(this.upgradePropose, other.upgradePropose)) return false;

        return true;
    }
}
