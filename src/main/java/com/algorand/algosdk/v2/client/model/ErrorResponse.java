package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Objects;

/**
 * Response for errors
 */
public class ErrorResponse extends PathResponse {

    @JsonProperty("data")
    public HashMap<String,Object> data;

    @JsonProperty("message")
    public String message;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        ErrorResponse other = (ErrorResponse) o;
        if (!Objects.deepEquals(this.data, other.data)) return false;
        if (!Objects.deepEquals(this.message, other.message)) return false;

        return true;
    }
}
