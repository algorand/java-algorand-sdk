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
     * The boolean flag that lifts the limit on log opcode during simulation.
     */
    @JsonProperty("lift-log-limits")
    public Boolean liftLogLimits;

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
        if (!Objects.deepEquals(this.liftLogLimits, other.liftLogLimits)) return false;
        if (!Objects.deepEquals(this.txnGroups, other.txnGroups)) return false;

        return true;
    }
}
