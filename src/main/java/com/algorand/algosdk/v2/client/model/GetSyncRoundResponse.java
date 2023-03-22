package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response containing the ledger's minimum sync round
 */
public class GetSyncRoundResponse extends PathResponse {

    /**
     * The minimum sync round for the ledger.
     */
    @JsonProperty("round")
    public Long round;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        GetSyncRoundResponse other = (GetSyncRoundResponse) o;
        if (!Objects.deepEquals(this.round, other.round)) return false;

        return true;
    }
}
