package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * App which was created or deleted.
 */
public class ModifiedApp extends PathResponse {

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
     * App Id
     */
    @JsonProperty("id")
    public Long id;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        ModifiedApp other = (ModifiedApp) o;
        if (!Objects.deepEquals(this.created, other.created)) return false;
        if (!Objects.deepEquals(this.creator, other.creator)) return false;
        if (!Objects.deepEquals(this.id, other.id)) return false;

        return true;
    }
}
