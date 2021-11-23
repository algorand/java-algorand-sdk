package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.*;

public class CatchpointAbortResponse extends PathResponse {

    /**
     * Catchup abort response string
     */
    @JsonProperty("catchup-message")
    public String catchupMessage;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        CatchpointAbortResponse other = (CatchpointAbortResponse) o;
        if (!Objects.deepEquals(this.catchupMessage, other.catchupMessage)) return false;

        return true;
    }
}
