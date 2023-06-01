package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The set of trace information and effect from evaluating a single opcode.
 */
public class SimulationOpcodeTraceUnit extends PathResponse {

    /**
     * The program counter of the current opcode being evaluated.
     */
    @JsonProperty("pc")
    public Long pc;

    /**
     * The indexes of the traces for inner transactions spawned by this opcode, if any.
     */
    @JsonProperty("spawned-inners")
    public List<Long> spawnedInners = new ArrayList<Long>();

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        SimulationOpcodeTraceUnit other = (SimulationOpcodeTraceUnit) o;
        if (!Objects.deepEquals(this.pc, other.pc)) return false;
        if (!Objects.deepEquals(this.spawnedInners, other.spawnedInners)) return false;

        return true;
    }
}
