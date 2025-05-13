package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Fields relating to a protocol upgrade.
 */
public class BlockUpgradeState extends PathResponse {

    /**
     * (proto) The current protocol version.
     */
    @JsonProperty("current-protocol")
    public String currentProtocol;

    /**
     * (nextproto) The next proposed protocol version.
     */
    @JsonProperty("next-protocol")
    public String nextProtocol;

    /**
     * (nextyes) Number of blocks which approved the protocol upgrade.
     */
    @JsonProperty("next-protocol-approvals")
    public Long nextProtocolApprovals;

    /**
     * (nextswitch) Round on which the protocol upgrade will take effect.
     */
    @JsonProperty("next-protocol-switch-on")
    public Long nextProtocolSwitchOn;

    /**
     * (nextbefore) Deadline round for this protocol upgrade (No votes will be consider
     * after this round).
     */
    @JsonProperty("next-protocol-vote-before")
    public Long nextProtocolVoteBefore;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        BlockUpgradeState other = (BlockUpgradeState) o;
        if (!Objects.deepEquals(this.currentProtocol, other.currentProtocol)) return false;
        if (!Objects.deepEquals(this.nextProtocol, other.nextProtocol)) return false;
        if (!Objects.deepEquals(this.nextProtocolApprovals, other.nextProtocolApprovals)) return false;
        if (!Objects.deepEquals(this.nextProtocolSwitchOn, other.nextProtocolSwitchOn)) return false;
        if (!Objects.deepEquals(this.nextProtocolVoteBefore, other.nextProtocolVoteBefore)) return false;

        return true;
    }
}
