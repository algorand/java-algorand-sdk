package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Hash of a block header.
 */
public class BlockHashResponse extends PathResponse {

    /**
     * Block header hash.
     */
    @JsonProperty("blockHash")
    public String blockHash;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        BlockHashResponse other = (BlockHashResponse) o;
        if (!Objects.deepEquals(this.blockHash, other.blockHash)) return false;

        return true;
    }
}
