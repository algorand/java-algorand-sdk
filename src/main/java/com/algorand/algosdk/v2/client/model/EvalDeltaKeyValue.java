package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Key-value pairs for StateDelta.
 */
public class EvalDeltaKeyValue extends PathResponse {

    @JsonProperty("key")
    public String key;

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
