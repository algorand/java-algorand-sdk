package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Objects;

/**
 * Encoded block object.
 */
public class BlockResponse extends PathResponse {

    /**
     * Block header data.
     */
    @JsonProperty("block")
    public HashMap<String,Object> block;

    /**
     * Optional certificate object. This is only included when the format is set to
     * message pack.
     */
    @JsonProperty("cert")
    public HashMap<String,Object> cert;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        BlockResponse other = (BlockResponse) o;
        if (!Objects.deepEquals(this.block, other.block)) return false;
        if (!Objects.deepEquals(this.cert, other.cert)) return false;

        return true;
    }
}
