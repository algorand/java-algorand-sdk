package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CatchpointStartResponse extends PathResponse {

    /**
     * Catchup start response string
     */
    @JsonProperty("catchup-message")
    public String catchupMessage;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        CatchpointStartResponse other = (CatchpointStartResponse) o;
        if (!Objects.deepEquals(this.catchupMessage, other.catchupMessage)) return false;

        return true;
    }
}
