package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Result of a transaction group simulation.
 */
public class SimulateResponse extends PathResponse {

    /**
     * The set of parameters and limits override during simulation. If this set of
     * parameters is present, then evaluation parameters may differ from standard
     * evaluation in certain ways.
     */
    @JsonProperty("eval-overrides")
    public SimulationEvalOverrides evalOverrides;

    /**
     * An object that configures simulation execution trace.
     */
    @JsonProperty("exec-trace-config")
    public SimulateTraceConfig execTraceConfig;

    /**
     * Initial states of resources that were accessed during simulation.
     */
    @JsonProperty("initial-states")
    public SimulateInitialStates initialStates;

    /**
     * The round immediately preceding this simulation. State changes through this
     * round were used to run this simulation.
     */
    @JsonProperty("last-round")
    public Long lastRound;

    /**
     * Total fees paid across all top-level transaction groups and their descendants.
     */
    @JsonProperty("total-fees-paid")
    public Long totalFeesPaid;

    /**
     * Total fee usage across all top-level transaction groups and their descendants,
     * in millionths of a basic transaction fee unit.
     */
    @JsonProperty("total-usage")
    public Long totalUsage;

    /**
     * A result object for each transaction group that was simulated.
     */
    @JsonProperty("txn-groups")
    public List<SimulateTransactionGroupResult> txnGroups = new ArrayList<SimulateTransactionGroupResult>();

    /**
     * The version of this response object.
     */
    @JsonProperty("version")
    public Long version;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        SimulateResponse other = (SimulateResponse) o;
        if (!Objects.deepEquals(this.evalOverrides, other.evalOverrides)) return false;
        if (!Objects.deepEquals(this.execTraceConfig, other.execTraceConfig)) return false;
        if (!Objects.deepEquals(this.initialStates, other.initialStates)) return false;
        if (!Objects.deepEquals(this.lastRound, other.lastRound)) return false;
        if (!Objects.deepEquals(this.totalFeesPaid, other.totalFeesPaid)) return false;
        if (!Objects.deepEquals(this.totalUsage, other.totalUsage)) return false;
        if (!Objects.deepEquals(this.txnGroups, other.txnGroups)) return false;
        if (!Objects.deepEquals(this.version, other.version)) return false;

        return true;
    }
}
