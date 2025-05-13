package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Teal disassembly Result
 */
public class DisassembleResponse extends PathResponse {

    /**
     * disassembled Teal code
     */
    @JsonProperty("result")
    public String result;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        DisassembleResponse other = (DisassembleResponse) o;
        if (!Objects.deepEquals(this.result, other.result)) return false;

        return true;
    }
}
