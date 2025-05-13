package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Simulation result for an atomic transaction group
 */
public class SimulateTransactionGroupResult extends PathResponse {

    /**
     * Total budget added during execution of app calls in the transaction group.
     */
    @JsonProperty("app-budget-added")
    public Long appBudgetAdded;

    /**
     * Total budget consumed during execution of app calls in the transaction group.
     */
    @JsonProperty("app-budget-consumed")
    public Long appBudgetConsumed;

    /**
     * If present, indicates which transaction in this group caused the failure. This
     * array represents the path to the failing transaction. Indexes are zero based,
     * the first element indicates the top-level transaction, and successive elements
     * indicate deeper inner transactions.
     */
    @JsonProperty("failed-at")
    public List<Long> failedAt = new ArrayList<Long>();

    /**
     * If present, indicates that the transaction group failed and specifies why that
     * happened
     */
    @JsonProperty("failure-message")
    public String failureMessage;

    /**
     * Simulation result for individual transactions
     */
    @JsonProperty("txn-results")
    public List<SimulateTransactionResult> txnResults = new ArrayList<SimulateTransactionResult>();

    /**
     * These are resources that were accessed by this group that would normally have
     * caused failure, but were allowed in simulation. Depending on where this object
     * is in the response, the unnamed resources it contains may or may not qualify for
     * group resource sharing. If this is a field in SimulateTransactionGroupResult,
     * the resources do qualify, but if this is a field in SimulateTransactionResult,
     * they do not qualify. In order to make this group valid for actual submission,
     * resources that qualify for group sharing can be made available by any
     * transaction of the group; otherwise, resources must be placed in the same
     * transaction which accessed them.
     */
    @JsonProperty("unnamed-resources-accessed")
    public SimulateUnnamedResourcesAccessed unnamedResourcesAccessed;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        SimulateTransactionGroupResult other = (SimulateTransactionGroupResult) o;
        if (!Objects.deepEquals(this.appBudgetAdded, other.appBudgetAdded)) return false;
        if (!Objects.deepEquals(this.appBudgetConsumed, other.appBudgetConsumed)) return false;
        if (!Objects.deepEquals(this.failedAt, other.failedAt)) return false;
        if (!Objects.deepEquals(this.failureMessage, other.failureMessage)) return false;
        if (!Objects.deepEquals(this.txnResults, other.txnResults)) return false;
        if (!Objects.deepEquals(this.unnamedResourcesAccessed, other.unnamedResourcesAccessed)) return false;

        return true;
    }
}
