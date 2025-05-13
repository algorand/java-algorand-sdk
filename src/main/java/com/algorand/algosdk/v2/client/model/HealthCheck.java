package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * A health check response.
 */
public class HealthCheck extends PathResponse {

    @JsonProperty("data")
    public HashMap<String,Object> data;

    @JsonProperty("db-available")
    public Boolean dbAvailable;

    @JsonProperty("errors")
    public List<String> errors = new ArrayList<String>();

    @JsonProperty("is-migrating")
    public Boolean isMigrating;

    @JsonProperty("message")
    public String message;

    @JsonProperty("round")
    public Long round;

    /**
     * Current version.
     */
    @JsonProperty("version")
    public String version;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        HealthCheck other = (HealthCheck) o;
        if (!Objects.deepEquals(this.data, other.data)) return false;
        if (!Objects.deepEquals(this.dbAvailable, other.dbAvailable)) return false;
        if (!Objects.deepEquals(this.errors, other.errors)) return false;
        if (!Objects.deepEquals(this.isMigrating, other.isMigrating)) return false;
        if (!Objects.deepEquals(this.message, other.message)) return false;
        if (!Objects.deepEquals(this.round, other.round)) return false;
        if (!Objects.deepEquals(this.version, other.version)) return false;

        return true;
    }
}
