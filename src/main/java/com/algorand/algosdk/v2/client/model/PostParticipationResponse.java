package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Participation ID of the submission
 */
public class PostParticipationResponse extends PathResponse {

    /**
     * encoding of the participation ID.
     */
    @JsonProperty("partId")
    public String partId;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        PostParticipationResponse other = (PostParticipationResponse) o;
        if (!Objects.deepEquals(this.partId, other.partId)) return false;

        return true;
    }
}
