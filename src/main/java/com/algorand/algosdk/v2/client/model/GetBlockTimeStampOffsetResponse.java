package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response containing the timestamp offset in seconds
 */
public class GetBlockTimeStampOffsetResponse extends PathResponse {

    /**
     * Timestamp offset in seconds.
     */
    @JsonProperty("offset")
    public Long offset;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        GetBlockTimeStampOffsetResponse other = (GetBlockTimeStampOffsetResponse) o;
        if (!Objects.deepEquals(this.offset, other.offset)) return false;

        return true;
    }
}
