package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A list of participation keys
 */
public class ParticipationKeysResponse extends PathResponse {

    @JsonProperty("participation-keys")
    public List<ParticipationKey> participationKeys = new ArrayList<ParticipationKey>();

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        ParticipationKeysResponse other = (ParticipationKeysResponse) o;
        if (!Objects.deepEquals(this.participationKeys, other.participationKeys)) return false;

        return true;
    }
}
