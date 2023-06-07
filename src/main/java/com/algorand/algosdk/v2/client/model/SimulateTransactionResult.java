package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Simulation result for an individual transaction
 */
public class SimulateTransactionResult extends PathResponse {

    /**
     * Budget used during execution of an app call transaction. This value includes
     * budged used by inner app calls spawned by this transaction.
     */
    @JsonProperty("app-budget-consumed")
    public Long appBudgetConsumed;

    /**
     * The execution trace of calling an app or a logic sig, containing the inner app
     * call trace in a recursive way.
     */
    @JsonProperty("exec-trace")
    public SimulationTransactionExecTrace execTrace;

    /**
     * Budget used during execution of a logic sig transaction.
     */
    @JsonProperty("logic-sig-budget-consumed")
    public Long logicSigBudgetConsumed;

    /**
     * Details about a pending transaction. If the transaction was recently confirmed,
     * includes confirmation details like the round and reward details.
     */
    @JsonProperty("txn-result")
    public PendingTransactionResponse txnResult;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        SimulateTransactionResult other = (SimulateTransactionResult) o;
        if (!Objects.deepEquals(this.appBudgetConsumed, other.appBudgetConsumed)) return false;
        if (!Objects.deepEquals(this.execTrace, other.execTrace)) return false;
        if (!Objects.deepEquals(this.logicSigBudgetConsumed, other.logicSigBudgetConsumed)) return false;
        if (!Objects.deepEquals(this.txnResult, other.txnResult)) return false;

        return true;
    }
}
