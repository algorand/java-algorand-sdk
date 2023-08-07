package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A write operation into a scratch slot.
 */
public class ScratchChange extends PathResponse {

    /**
     * Represents an AVM value.
     */
    @JsonProperty("new-value")
    public AvmValue newValue;

    /**
     * The scratch slot written.
     */
    @JsonProperty("slot")
    public Long slot;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        ScratchChange other = (ScratchChange) o;
        if (!Objects.deepEquals(this.newValue, other.newValue)) return false;
        if (!Objects.deepEquals(this.slot, other.slot)) return false;

        return true;
    }
}
