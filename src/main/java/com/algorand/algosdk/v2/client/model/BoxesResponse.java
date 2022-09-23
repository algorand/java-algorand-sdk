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

    /**
     * (appidx) application index.
     */
    @JsonProperty("application-id")
    public Long applicationId;

    @JsonProperty("boxes")
    public List<BoxDescriptor> boxes = new ArrayList<BoxDescriptor>();

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

        BoxesResponse other = (BoxesResponse) o;
        if (!Objects.deepEquals(this.applicationId, other.applicationId)) return false;
        if (!Objects.deepEquals(this.boxes, other.boxes)) return false;
        if (!Objects.deepEquals(this.nextToken, other.nextToken)) return false;

        return true;
    }
}
