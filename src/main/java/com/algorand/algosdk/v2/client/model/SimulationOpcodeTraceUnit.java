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
     * The writes into scratch slots.
     */
    @JsonProperty("scratch-changes")
    public List<ScratchChange> scratchChanges = new ArrayList<ScratchChange>();

    /**
     * The indexes of the traces for inner transactions spawned by this opcode, if any.
     */
    @JsonProperty("spawned-inners")
    public List<Long> spawnedInners = new ArrayList<Long>();

    /**
     * The values added by this opcode to the stack.
     */
    @JsonProperty("stack-additions")
    public List<AvmValue> stackAdditions = new ArrayList<AvmValue>();

    /**
     * The number of deleted stack values by this opcode.
     */
    @JsonProperty("stack-pop-count")
    public Long stackPopCount;

    /**
     * The operations against the current application's states.
     */
    @JsonProperty("state-changes")
    public List<ApplicationStateOperation> stateChanges = new ArrayList<ApplicationStateOperation>();

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        SimulationOpcodeTraceUnit other = (SimulationOpcodeTraceUnit) o;
        if (!Objects.deepEquals(this.pc, other.pc)) return false;
        if (!Objects.deepEquals(this.scratchChanges, other.scratchChanges)) return false;
        if (!Objects.deepEquals(this.spawnedInners, other.spawnedInners)) return false;
        if (!Objects.deepEquals(this.stackAdditions, other.stackAdditions)) return false;
        if (!Objects.deepEquals(this.stackPopCount, other.stackPopCount)) return false;
        if (!Objects.deepEquals(this.stateChanges, other.stateChanges)) return false;

        return true;
    }
}
