package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Simulation result for an individual transaction
 */
public class SimulateTransactionResult extends PathResponse {

    /**
     * A boolean indicating whether this transaction is missing signatures
     */
    @JsonProperty("missing-signature")
    public Boolean missingSignature;

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
        if (!Objects.deepEquals(this.missingSignature, other.missingSignature)) return false;
        if (!Objects.deepEquals(this.txnResult, other.txnResult)) return false;

        return true;
    }
}
