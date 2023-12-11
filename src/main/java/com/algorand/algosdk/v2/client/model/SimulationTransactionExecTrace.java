package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The execution trace of calling an app or a logic sig, containing the inner app
 * call trace in a recursive way.
 */
public class SimulationTransactionExecTrace extends PathResponse {

    /**
     * SHA512_256 hash digest of the approval program executed in transaction.
     */
    @JsonProperty("approval-program-hash")
    public void approvalProgramHash(String base64Encoded) {
        this.approvalProgramHash = Encoder.decodeFromBase64(base64Encoded);
    }
    public String approvalProgramHash() {
        return Encoder.encodeToBase64(this.approvalProgramHash);
    }
    public byte[] approvalProgramHash;

    /**
     * Program trace that contains a trace of opcode effects in an approval program.
     */
    @JsonProperty("approval-program-trace")
    public List<SimulationOpcodeTraceUnit> approvalProgramTrace = new ArrayList<SimulationOpcodeTraceUnit>();

    /**
     * SHA512_256 hash digest of the clear state program executed in transaction.
     */
    @JsonProperty("clear-state-program-hash")
    public void clearStateProgramHash(String base64Encoded) {
        this.clearStateProgramHash = Encoder.decodeFromBase64(base64Encoded);
    }
    public String clearStateProgramHash() {
        return Encoder.encodeToBase64(this.clearStateProgramHash);
    }
    public byte[] clearStateProgramHash;

    /**
     * Program trace that contains a trace of opcode effects in a clear state program.
     */
    @JsonProperty("clear-state-program-trace")
    public List<SimulationOpcodeTraceUnit> clearStateProgramTrace = new ArrayList<SimulationOpcodeTraceUnit>();

    /**
     * If true, indicates that the clear state program failed and any persistent state
     * changes it produced should be reverted once the program exits.
     */
    @JsonProperty("clear-state-rollback")
    public Boolean clearStateRollback;

    /**
     * The error message explaining why the clear state program failed. This field will
     * only be populated if clear-state-rollback is true and the failure was due to an
     * execution error.
     */
    @JsonProperty("clear-state-rollback-error")
    public String clearStateRollbackError;

    /**
     * An array of SimulationTransactionExecTrace representing the execution trace of
     * any inner transactions executed.
     */
    @JsonProperty("inner-trace")
    public List<SimulationTransactionExecTrace> innerTrace = new ArrayList<SimulationTransactionExecTrace>();

    /**
     * SHA512_256 hash digest of the logic sig executed in transaction.
     */
    @JsonProperty("logic-sig-hash")
    public void logicSigHash(String base64Encoded) {
        this.logicSigHash = Encoder.decodeFromBase64(base64Encoded);
    }
    public String logicSigHash() {
        return Encoder.encodeToBase64(this.logicSigHash);
    }
    public byte[] logicSigHash;

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
        if (!Objects.deepEquals(this.approvalProgramHash, other.approvalProgramHash)) return false;
        if (!Objects.deepEquals(this.approvalProgramTrace, other.approvalProgramTrace)) return false;
        if (!Objects.deepEquals(this.clearStateProgramHash, other.clearStateProgramHash)) return false;
        if (!Objects.deepEquals(this.clearStateProgramTrace, other.clearStateProgramTrace)) return false;
        if (!Objects.deepEquals(this.clearStateRollback, other.clearStateRollback)) return false;
        if (!Objects.deepEquals(this.clearStateRollbackError, other.clearStateRollbackError)) return false;
        if (!Objects.deepEquals(this.innerTrace, other.innerTrace)) return false;
        if (!Objects.deepEquals(this.logicSigHash, other.logicSigHash)) return false;
        if (!Objects.deepEquals(this.logicSigTrace, other.logicSigTrace)) return false;

        return true;
    }
}
