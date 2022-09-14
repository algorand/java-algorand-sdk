package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Participation account data that needs to be checked/acted on by the network.
 */
public class ParticipationUpdates extends PathResponse {

    /**
     * (partupdrmv) a list of online accounts that needs to be converted to offline
     * since their participation key expired.
     */
    @JsonProperty("expired-participation-accounts")
    public List<String> expiredParticipationAccounts = new ArrayList<String>();

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        ParticipationUpdates other = (ParticipationUpdates) o;
        if (!Objects.deepEquals(this.expiredParticipationAccounts, other.expiredParticipationAccounts)) return false;

        return true;
    }
}
