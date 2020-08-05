package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Teal compile Result
 */
public class CompileResponse extends PathResponse {

    /**
     * base32 SHA512_256 of program bytes (Address style)
     */
    @JsonProperty("hash")
    public String hash;

    /**
     * base64 encoded program bytes
     */
    @JsonProperty("result")
    public String result;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        CompileResponse other = (CompileResponse) o;
        if (!Objects.deepEquals(this.hash, other.hash)) return false;
        if (!Objects.deepEquals(this.result, other.result)) return false;

        return true;
    }
}
