package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Application index and its parameters 
 */
public class Application extends PathResponse {

    /**
     * (appidx) application index. 
     */
    @JsonProperty("app-index")
    public Long appIndex;

    /**
     * (appparams) application parameters. 
     */
    @JsonProperty("app-params")
    public ApplicationParams appParams;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        Application other = (Application) o;
        if (!Objects.deepEquals(this.appIndex, other.appIndex)) return false;
        if (!Objects.deepEquals(this.appParams, other.appParams)) return false;

        return true;
    }
}
