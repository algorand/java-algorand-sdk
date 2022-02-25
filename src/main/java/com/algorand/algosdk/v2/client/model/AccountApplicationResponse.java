package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AccountApplicationResponse describes the account's application local state and
 * global state (AppLocalState and AppParams, if either exists) for a specific
 * application ID. Global state will only be returned if the provided address is
 * the application's creator.
 */
public class AccountApplicationResponse extends PathResponse {

    /**
     * (appl) the application local data stored in this account.
     * The raw account uses `AppLocalState` for this type.
     */
    @JsonProperty("app-local-state")
    public ApplicationLocalState appLocalState;

    /**
     * (appp) parameters of the application created by this account including app
     * global data.
     * The raw account uses `AppParams` for this type.
     */
    @JsonProperty("created-app")
    public ApplicationParams createdApp;

    /**
     * The round for which this information is relevant.
     */
    @JsonProperty("round")
    public Long round;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        AccountApplicationResponse other = (AccountApplicationResponse) o;
        if (!Objects.deepEquals(this.appLocalState, other.appLocalState)) return false;
        if (!Objects.deepEquals(this.createdApp, other.createdApp)) return false;
        if (!Objects.deepEquals(this.round, other.round)) return false;

        return true;
    }
}
