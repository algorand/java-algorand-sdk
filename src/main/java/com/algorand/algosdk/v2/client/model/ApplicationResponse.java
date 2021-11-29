package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApplicationResponse extends PathResponse {

    /**
     * Application index and its parameters
     */
    @JsonProperty("application")
    public Application application;

    /**
     * Round at which the results were computed.
     */
    @JsonProperty("current-round")
    public Long currentRound;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        ApplicationResponse other = (ApplicationResponse) o;
        if (!Objects.deepEquals(this.application, other.application)) return false;
        if (!Objects.deepEquals(this.currentRound, other.currentRound)) return false;

        return true;
    }
}
