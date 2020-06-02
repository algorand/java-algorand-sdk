package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Pair of application index and application local state 
 */
public class ApplicationLocalStates extends PathResponse {

    @JsonProperty("app-index")
    public Long appIndex;

    @JsonProperty("state")
    public ApplicationLocalState state;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        ApplicationLocalStates other = (ApplicationLocalStates) o;
        if (!Objects.deepEquals(this.appIndex, other.appIndex)) return false;
        if (!Objects.deepEquals(this.state, other.state)) return false;

        return true;
    }
}
