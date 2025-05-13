package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Initial states of resources that were accessed during simulation.
 */
public class SimulateInitialStates extends PathResponse {

    /**
     * The initial states of accessed application before simulation. The order of this
     * array is arbitrary.
     */
    @JsonProperty("app-initial-states")
    public List<ApplicationInitialStates> appInitialStates = new ArrayList<ApplicationInitialStates>();

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        SimulateInitialStates other = (SimulateInitialStates) o;
        if (!Objects.deepEquals(this.appInitialStates, other.appInitialStates)) return false;

        return true;
    }
}
