package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Key-value pairs for StateDelta.
 */
public class EvalDeltaKeyValue extends PathResponse {

    @JsonProperty("key")
    public String key;

    /**
     * Represents a TEAL value delta.
     */
    @JsonProperty("value")
    public EvalDelta value;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        EvalDeltaKeyValue other = (EvalDeltaKeyValue) o;
        if (!Objects.deepEquals(this.key, other.key)) return false;
        if (!Objects.deepEquals(this.value, other.value)) return false;

        return true;
    }
}
