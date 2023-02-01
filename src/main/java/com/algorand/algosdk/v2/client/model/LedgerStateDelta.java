package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains ledger updates.
 */
public class LedgerStateDelta extends PathResponse {

    /**
     * AccountDeltas object
     */
    @JsonProperty("accts")
    public AccountDeltas accts;

    /**
     * Array of KV Deltas
     */
    @JsonProperty("kv-mods")
    public List<KvDelta> kvMods = new ArrayList<KvDelta>();

    /**
     * List of modified Apps
     */
    @JsonProperty("modified-apps")
    public List<ModifiedApp> modifiedApps = new ArrayList<ModifiedApp>();

    /**
     * List of modified Assets
     */
    @JsonProperty("modified-assets")
    public List<ModifiedAsset> modifiedAssets = new ArrayList<ModifiedAsset>();

    /**
     * Previous block timestamp
     */
    @JsonProperty("prev-timestamp")
    public Long prevTimestamp;

    /**
     * Next round for which we expect a state proof
     */
    @JsonProperty("state-proof-next")
    public Long stateProofNext;

    /**
     * Account Totals
     */
    @JsonProperty("totals")
    public AccountTotals totals;

    /**
     * List of transaction leases
     */
    @JsonProperty("tx-leases")
    public List<TxLease> txLeases = new ArrayList<TxLease>();

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        LedgerStateDelta other = (LedgerStateDelta) o;
        if (!Objects.deepEquals(this.accts, other.accts)) return false;
        if (!Objects.deepEquals(this.kvMods, other.kvMods)) return false;
        if (!Objects.deepEquals(this.modifiedApps, other.modifiedApps)) return false;
        if (!Objects.deepEquals(this.modifiedAssets, other.modifiedAssets)) return false;
        if (!Objects.deepEquals(this.prevTimestamp, other.prevTimestamp)) return false;
        if (!Objects.deepEquals(this.stateProofNext, other.stateProofNext)) return false;
        if (!Objects.deepEquals(this.totals, other.totals)) return false;
        if (!Objects.deepEquals(this.txLeases, other.txLeases)) return false;

        return true;
    }
}
