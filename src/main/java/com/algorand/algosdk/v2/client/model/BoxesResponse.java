package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Box names of an application
 */
public class BoxesResponse extends PathResponse {

    @JsonProperty("boxes")
    public List<BoxDescriptor> boxes = new ArrayList<BoxDescriptor>();

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        BoxesResponse other = (BoxesResponse) o;
        if (!Objects.deepEquals(this.boxes, other.boxes)) return false;

        return true;
    }
}
