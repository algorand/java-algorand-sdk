package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Simulation result for an atomic transaction group
 */
public class SimulateTransactionGroupResult extends PathResponse {

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

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        SimulateTransactionGroupResult other = (SimulateTransactionGroupResult) o;
        if (!Objects.deepEquals(this.failedAt, other.failedAt)) return false;
        if (!Objects.deepEquals(this.failureMessage, other.failureMessage)) return false;
        if (!Objects.deepEquals(this.txnResults, other.txnResults)) return false;

        return true;
    }
}
