package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.algorand.algosdk.v2.client.model.TealValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a key-value pair in an application store.
 */
public class TealKeyValue extends PathResponse {

    @JsonProperty("key")
    public String key;

    @JsonProperty("value")
    public TealValue value;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        TealKeyValue other = (TealKeyValue) o;
        if (!Objects.deepEquals(this.key, other.key)) return false;
        if (!Objects.deepEquals(this.value, other.value)) return false;

        return true;
    }
}
