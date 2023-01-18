package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Asset which was created or deleted.
 */
public class ModifiedAsset extends PathResponse {

    /**
     * Created if true, deleted if false
     */
    @JsonProperty("created")
    public Boolean created;

    /**
     * Address of the creator.
     */
    @JsonProperty("creator")
    public String creator;

    /**
     * Asset Id
     */
    @JsonProperty("id")
    public Long id;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        ModifiedAsset other = (ModifiedAsset) o;
        if (!Objects.deepEquals(this.created, other.created)) return false;
        if (!Objects.deepEquals(this.creator, other.creator)) return false;
        if (!Objects.deepEquals(this.id, other.id)) return false;

        return true;
    }
}
