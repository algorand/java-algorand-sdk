package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A health check response. 
 */
public class HealthCheck extends PathResponse {

    @JsonProperty("data")
    public String data;

    @JsonProperty("message")
    public String message;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        HealthCheck other = (HealthCheck) o;
        if (!Objects.deepEquals(this.data, other.data)) return false;
        if (!Objects.deepEquals(this.message, other.message)) return false;

        return true;
    }
}
