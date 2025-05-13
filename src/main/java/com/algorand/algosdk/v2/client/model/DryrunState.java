package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Stores the TEAL eval step data
 */
public class DryrunState extends PathResponse {

    /**
     * Evaluation error if any
     */
    @JsonProperty("error")
    public String error;

    /**
     * Line number
     */
    @JsonProperty("line")
    public Long line;

    /**
     * Program counter
     */
    @JsonProperty("pc")
    public Long pc;

    @JsonProperty("scratch")
    public List<TealValue> scratch = new ArrayList<TealValue>();

    @JsonProperty("stack")
    public List<TealValue> stack = new ArrayList<TealValue>();

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        DryrunState other = (DryrunState) o;
        if (!Objects.deepEquals(this.error, other.error)) return false;
        if (!Objects.deepEquals(this.line, other.line)) return false;
        if (!Objects.deepEquals(this.pc, other.pc)) return false;
        if (!Objects.deepEquals(this.scratch, other.scratch)) return false;
        if (!Objects.deepEquals(this.stack, other.stack)) return false;

        return true;
    }
}
