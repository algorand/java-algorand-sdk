package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AccountApplicationResource describes the account's application resource (local
 * state and params if the account is the creator) for a specific application ID.
 */
public class AccountApplicationResource extends PathResponse {

    /**
     * (appl) the application local data stored in this account.
     * The raw account uses `AppLocalState` for this type.
     */
    @JsonProperty("app-local-state")
    public ApplicationLocalState appLocalState;

    /**
     * Round when the account opted into or created the application.
     */
    @JsonProperty("created-at-round")
    public Long createdAtRound;

    /**
     * Whether the application has been deleted.
     */
    @JsonProperty("deleted")
    public Boolean deleted;

    /**
     * The application ID.
     */
    @JsonProperty("id")
    public Long id;

    /**
     * (appp) parameters of the application created by this account including app
     * global data.
     * The raw account uses `AppParams` for this type.
     * Only present if the account is the creator and `include=params` is specified.
     */
    @JsonProperty("params")
    public ApplicationParams params;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        AccountApplicationResource other = (AccountApplicationResource) o;
        if (!Objects.deepEquals(this.appLocalState, other.appLocalState)) return false;
        if (!Objects.deepEquals(this.createdAtRound, other.createdAtRound)) return false;
        if (!Objects.deepEquals(this.deleted, other.deleted)) return false;
        if (!Objects.deepEquals(this.id, other.id)) return false;
        if (!Objects.deepEquals(this.params, other.params)) return false;

        return true;
    }
}
