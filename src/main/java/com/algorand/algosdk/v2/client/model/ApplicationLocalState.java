package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.algorand.algosdk.v2.client.model.ApplicationStateSchema;
import com.algorand.algosdk.v2.client.model.TealKeyValue;
import com.algorand.algosdk.v2.client.model.TealKeyValueStore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Stores local state associated with an application.
 */
public class ApplicationLocalState extends PathResponse {

    /**
     * (tkv) storage.
     */
    @JsonProperty("key-value")
    public List<TealKeyValue> keyValue = new ArrayList<TealKeyValue>();

    /**
     * (hsch) schema.
     */
    @JsonProperty("schema")
    public ApplicationStateSchema schema;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        ApplicationLocalState other = (ApplicationLocalState) o;
        if (!Objects.deepEquals(this.keyValue, other.keyValue)) return false;
        if (!Objects.deepEquals(this.schema, other.schema)) return false;

        return true;
    }
}
