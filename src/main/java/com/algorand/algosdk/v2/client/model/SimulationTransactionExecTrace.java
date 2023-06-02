package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The execution trace of calling an app or a logic sig, containing the inner app
 * call trace in a recursive way.
 */
public class SimulationTransactionExecTrace extends PathResponse {

    /**
     * Program trace that contains a trace of opcode effects in an approval program.
     */
    @JsonProperty("approval-program-trace")
    public List<SimulationOpcodeTraceUnit> approvalProgramTrace = new ArrayList<SimulationOpcodeTraceUnit>();

    /**
     * Program trace that contains a trace of opcode effects in a clear state program.
     */
    @JsonProperty("clear-state-program-trace")
    public List<SimulationOpcodeTraceUnit> clearStateProgramTrace = new ArrayList<SimulationOpcodeTraceUnit>();

    /**
     * An array of SimulationTransactionExecTrace representing the execution trace of
     * any inner transactions executed.
     */
    @JsonProperty("inner-trace")
    public List<SimulationTransactionExecTrace> innerTrace = new ArrayList<SimulationTransactionExecTrace>();

    /**
     * Program trace that contains a trace of opcode effects in a logic sig.
     */
    @JsonProperty("logic-sig-trace")
    public List<SimulationOpcodeTraceUnit> logicSigTrace = new ArrayList<SimulationOpcodeTraceUnit>();

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        SimulationTransactionExecTrace other = (SimulationTransactionExecTrace) o;
        if (!Objects.deepEquals(this.approvalProgramTrace, other.approvalProgramTrace)) return false;
        if (!Objects.deepEquals(this.clearStateProgramTrace, other.clearStateProgramTrace)) return false;
        if (!Objects.deepEquals(this.innerTrace, other.innerTrace)) return false;
        if (!Objects.deepEquals(this.logicSigTrace, other.logicSigTrace)) return false;

        return true;
    }
}
