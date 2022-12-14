package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Exposes deltas for account based resources in a single round
 */
public class AccountDeltas extends PathResponse {

    /**
     * Array of Account updates for the round
     */
    @JsonProperty("accounts")
    public List<AccountBalanceRecord> accounts = new ArrayList<AccountBalanceRecord>();

    /**
     * Array of App updates for the round.
     */
    @JsonProperty("apps")
    public List<AppResourceRecord> apps = new ArrayList<AppResourceRecord>();

    /**
     * Array of Asset updates for the round.
     */
    @JsonProperty("assets")
    public List<AssetResourceRecord> assets = new ArrayList<AssetResourceRecord>();

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        AccountDeltas other = (AccountDeltas) o;
        if (!Objects.deepEquals(this.accounts, other.accounts)) return false;
        if (!Objects.deepEquals(this.apps, other.apps)) return false;
        if (!Objects.deepEquals(this.assets, other.assets)) return false;

        return true;
    }
}
