package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request type for simulation endpoint.
 */
public class SimulateRequest extends PathResponse {

    /**
     * Allow transactions without signatures to be simulated as if they had correct
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
        if (!Objects.deepEquals(this.txnGroups, other.txnGroups)) return false;

        return true;
    }
}
