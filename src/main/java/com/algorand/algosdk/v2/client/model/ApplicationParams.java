package com.algorand.algosdk.v2.client.model;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Stores the global information associated with an application.
 */
public class ApplicationParams extends PathResponse {

    /**
     * (approv) approval program.
     */
    @JsonProperty("approval-program")
    public String approvalProgram;

    /**
     * (clearp) approval program.
     */
    @JsonProperty("clear-state-program")
    public String clearStateProgram;

    /**
     * The address that created this application. This is the address where the
     * parameters and global state for this application can be found.
     */
    @JsonProperty("creator")
    public void creator(String creator) throws NoSuchAlgorithmException {
        this.creator = new Address(creator);
    }
    @JsonProperty("creator")
    public String creator() throws NoSuchAlgorithmException {
        if (this.creator != null) {
            return this.creator.encodeAsString();
        } else {
            return null;
        }
    }
    public Address creator;

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
        if (!Objects.deepEquals(this.creator, other.creator)) return false;
        if (!Objects.deepEquals(this.globalState, other.globalState)) return false;
        if (!Objects.deepEquals(this.globalStateSchema, other.globalStateSchema)) return false;
        if (!Objects.deepEquals(this.localStateSchema, other.localStateSchema)) return false;

        return true;
    }
}
