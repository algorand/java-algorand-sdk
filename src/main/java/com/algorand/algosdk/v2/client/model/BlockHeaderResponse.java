package com.algorand.algosdk.v2.client.model;

import java.util.HashMap;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Block header.
 */
public class BlockHeaderResponse extends PathResponse {

    /**
     * Block header data.
     */
    @JsonProperty("blockHeader")
    public HashMap<String,Object> blockHeader;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        BlockHeaderResponse other = (BlockHeaderResponse) o;
        if (!Objects.deepEquals(this.blockHeader, other.blockHeader)) return false;

        return true;
    }
}
