package com.algorand.algosdk.v2.client.model;

import java.util.HashMap;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An error response with optional data field.
 */
public class Error extends PathResponse {

    @JsonProperty("data")
    public HashMap<String,Object> data;

    @JsonProperty("message")
    public String message;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        Error other = (Error) o;
        if (!Objects.deepEquals(this.data, other.data)) return false;
        if (!Objects.deepEquals(this.message, other.message)) return false;

        return true;
    }
}
