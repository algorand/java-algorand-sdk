package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Top level transaction IDs in a block.
 */
public class BlockTxidsResponse extends PathResponse {

    /**
     * Block transaction IDs.
     */
    @JsonProperty("blockTxids")
    public List<String> blockTxids = new ArrayList<String>();

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        BlockTxidsResponse other = (BlockTxidsResponse) o;
        if (!Objects.deepEquals(this.blockTxids, other.blockTxids)) return false;

        return true;
    }
}
