package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BlockHeadersResponse extends PathResponse {

    @JsonProperty("blocks")
    public List<Block> blocks = new ArrayList<Block>();

    /**
     * Round at which the results were computed.
     */
    @JsonProperty("current-round")
    public Long currentRound;

    /**
     * Used for pagination, when making another request provide this token with the
     * next parameter.
     */
    @JsonProperty("next-token")
    public String nextToken;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        BlockHeadersResponse other = (BlockHeadersResponse) o;
        if (!Objects.deepEquals(this.blocks, other.blocks)) return false;
        if (!Objects.deepEquals(this.currentRound, other.currentRound)) return false;
        if (!Objects.deepEquals(this.nextToken, other.nextToken)) return false;

        return true;
    }
}
