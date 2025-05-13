package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Request type for simulation endpoint.
 */
public class SimulateRequest extends PathResponse {

    /**
     * Allows transactions without signatures to be simulated as if they had correct
     * signatures.
     */
    @JsonProperty("allow-empty-signatures")
    public Boolean allowEmptySignatures;

    /**
     * Lifts limits on log opcode usage during simulation.
     */
    @JsonProperty("allow-more-logging")
    public Boolean allowMoreLogging;

    /**
     * Allows access to unnamed resources during simulation.
     */
    @JsonProperty("allow-unnamed-resources")
    public Boolean allowUnnamedResources;

    /**
     * An object that configures simulation execution trace.
     */
    @JsonProperty("exec-trace-config")
    public SimulateTraceConfig execTraceConfig;

    /**
     * Applies extra opcode budget during simulation for each transaction group.
     */
    @JsonProperty("extra-opcode-budget")
    public Long extraOpcodeBudget;

    /**
     * If true, signers for transactions that are missing signatures will be fixed
     * during evaluation.
     */
    @JsonProperty("fix-signers")
    public Boolean fixSigners;

    /**
     * If provided, specifies the round preceding the simulation. State changes through
     * this round will be used to run this simulation. Usually only the 4 most recent
     * rounds will be available (controlled by the node config value MaxAcctLookback).
     * If not specified, defaults to the latest available round.
     */
    @JsonProperty("round")
    public Long round;

    /**
     * The transaction groups to simulate.
     */
    @JsonProperty("txn-groups")
    public List<SimulateRequestTransactionGroup> txnGroups = new ArrayList<SimulateRequestTransactionGroup>();

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        SimulateRequest other = (SimulateRequest) o;
        if (!Objects.deepEquals(this.allowEmptySignatures, other.allowEmptySignatures)) return false;
        if (!Objects.deepEquals(this.allowMoreLogging, other.allowMoreLogging)) return false;
        if (!Objects.deepEquals(this.allowUnnamedResources, other.allowUnnamedResources)) return false;
        if (!Objects.deepEquals(this.execTraceConfig, other.execTraceConfig)) return false;
        if (!Objects.deepEquals(this.extraOpcodeBudget, other.extraOpcodeBudget)) return false;
        if (!Objects.deepEquals(this.fixSigners, other.fixSigners)) return false;
        if (!Objects.deepEquals(this.round, other.round)) return false;
        if (!Objects.deepEquals(this.txnGroups, other.txnGroups)) return false;

        return true;
    }
}
