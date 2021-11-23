package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.*;

/**
 * Application index and its parameters
 */
public class Application extends PathResponse {

    /**
     * Round when this application was created.
     */
    @JsonProperty("created-at-round")
    public java.math.BigInteger createdAtRound;

    /**
     * Whether or not this application is currently deleted.
     */
    @JsonProperty("deleted")
    public Boolean deleted;

    /**
     * Round when this application was deleted.
     */
    @JsonProperty("deleted-at-round")
    public java.math.BigInteger deletedAtRound;

    /**
     * (appidx) application index.
     */
    @JsonProperty("id")
    public Long id;

    /**
     * (appparams) application parameters.
     */
    @JsonProperty("params")
    public ApplicationParams params;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        Application other = (Application) o;
        if (!Objects.deepEquals(this.createdAtRound, other.createdAtRound)) return false;
        if (!Objects.deepEquals(this.deleted, other.deleted)) return false;
        if (!Objects.deepEquals(this.deletedAtRound, other.deletedAtRound)) return false;
        if (!Objects.deepEquals(this.id, other.id)) return false;
        if (!Objects.deepEquals(this.params, other.params)) return false;

        return true;
    }
}
