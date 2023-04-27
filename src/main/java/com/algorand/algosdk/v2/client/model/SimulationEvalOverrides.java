package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The set of parameters and limits override during simulation. If this set of
 * parameters is present, then evaluation parameters may differ from standard
 * evaluation in certain ways.
 */
public class SimulationEvalOverrides extends PathResponse {

    /**
     * If true, transactions without signatures are allowed and simulated as if they
     * were properly signed.
     */
    @JsonProperty("allow-empty-signatures")
    public Boolean allowEmptySignatures;

    /**
     * The maximum log calls one can make during simulation
     */
    @JsonProperty("max-log-calls")
    public java.math.BigInteger maxLogCalls;

    /**
     * The maximum byte number to log during simulation
     */
    @JsonProperty("max-log-size")
    public java.math.BigInteger maxLogSize;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        SimulationEvalOverrides other = (SimulationEvalOverrides) o;
        if (!Objects.deepEquals(this.allowEmptySignatures, other.allowEmptySignatures)) return false;
        if (!Objects.deepEquals(this.maxLogCalls, other.maxLogCalls)) return false;
        if (!Objects.deepEquals(this.maxLogSize, other.maxLogSize)) return false;

        return true;
    }
}
