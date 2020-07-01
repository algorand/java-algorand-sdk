package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.algorand.algosdk.v2.client.model.ApplicationStateSchema;
import com.algorand.algosdk.v2.client.model.TealKeyValue;
import com.algorand.algosdk.v2.client.model.TealKeyValueStore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Stores the global information associated with an application.
 */
public class ApplicationParams extends PathResponse {

    /**
     * (approv) approval program.
     */
    @JsonProperty("approval-program")
    public void approvalProgram(String base64Encoded) {
        this.approvalProgram = Encoder.decodeFromBase64(base64Encoded);
    }
    @JsonProperty("approval-program")
    public String approvalProgram() {
        return Encoder.encodeToBase64(this.approvalProgram);
    }
    public byte[] approvalProgram;

    /**
     * (clearp) approval program.
     */
    @JsonProperty("clear-state-program")
    public void clearStateProgram(String base64Encoded) {
        this.clearStateProgram = Encoder.decodeFromBase64(base64Encoded);
    }
    @JsonProperty("clear-state-program")
    public String clearStateProgram() {
        return Encoder.encodeToBase64(this.clearStateProgram);
    }
    public byte[] clearStateProgram;

    /**
     * [\gs) global schema
     */
    @JsonProperty("global-state")
    public List<TealKeyValue> globalState = new ArrayList<TealKeyValue>();

    /**
     * [\lsch) global schema
     */
    @JsonProperty("global-state-schema")
    public ApplicationStateSchema globalStateSchema;

    /**
     * [\lsch) local schema
     */
    @JsonProperty("local-state-schema")
    public ApplicationStateSchema localStateSchema;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        ApplicationParams other = (ApplicationParams) o;
        if (!Objects.deepEquals(this.approvalProgram, other.approvalProgram)) return false;
        if (!Objects.deepEquals(this.clearStateProgram, other.clearStateProgram)) return false;
        if (!Objects.deepEquals(this.globalState, other.globalState)) return false;
        if (!Objects.deepEquals(this.globalStateSchema, other.globalStateSchema)) return false;
        if (!Objects.deepEquals(this.localStateSchema, other.localStateSchema)) return false;

        return true;
    }
}
