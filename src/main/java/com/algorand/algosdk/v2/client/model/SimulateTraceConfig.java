package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An object that configures simulation execution trace.
 */
public class SimulateTraceConfig extends PathResponse {

    /**
     * A boolean option for opting in execution trace features simulation endpoint.
     */
    @JsonProperty("enable")
    public Boolean enable;

    /**
     * A boolean option enabling returning stack changes together with execution trace
     * during simulation.
     */
    @JsonProperty("stack-change")
    public Boolean stackChange;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        SimulateTraceConfig other = (SimulateTraceConfig) o;
        if (!Objects.deepEquals(this.enable, other.enable)) return false;
        if (!Objects.deepEquals(this.stackChange, other.stackChange)) return false;

        return true;
    }
}
