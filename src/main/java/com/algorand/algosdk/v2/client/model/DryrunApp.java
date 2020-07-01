package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.algorand.algosdk.v2.client.model.ApplicationParams;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DryrunApp holds global app state for a dryrun call.
 */
public class DryrunApp extends PathResponse {

    @JsonProperty("creator")
    public String creator;

    @JsonProperty("id")
    public java.math.BigInteger id;

    @JsonProperty("params")
    public ApplicationParams params;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        DryrunApp other = (DryrunApp) o;
        if (!Objects.deepEquals(this.creator, other.creator)) return false;
        if (!Objects.deepEquals(this.id, other.id)) return false;
        if (!Objects.deepEquals(this.params, other.params)) return false;

        return true;
    }
}
