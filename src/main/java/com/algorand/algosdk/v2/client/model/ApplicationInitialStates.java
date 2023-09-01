package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An application's initial global/local/box states that were accessed during
 * simulation.
 */
public class ApplicationInitialStates extends PathResponse {

    /**
     * An application's global/local/box state.
     */
    @JsonProperty("app-boxes")
    public ApplicationKVStorage appBoxes;

    /**
     * An application's global/local/box state.
     */
    @JsonProperty("app-globals")
    public ApplicationKVStorage appGlobals;

    /**
     * An application's initial local states tied to different accounts.
     */
    @JsonProperty("app-locals")
    public List<ApplicationKVStorage> appLocals = new ArrayList<ApplicationKVStorage>();

    /**
     * Application index.
     */
    @JsonProperty("id")
    public java.math.BigInteger id;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        ApplicationInitialStates other = (ApplicationInitialStates) o;
        if (!Objects.deepEquals(this.appBoxes, other.appBoxes)) return false;
        if (!Objects.deepEquals(this.appGlobals, other.appGlobals)) return false;
        if (!Objects.deepEquals(this.appLocals, other.appLocals)) return false;
        if (!Objects.deepEquals(this.id, other.id)) return false;

        return true;
    }
}
