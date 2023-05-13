package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains a ledger delta for a single transaction group
 */
public class LedgerStateDeltaForTransactionGroup extends PathResponse {

    /**
     * Ledger StateDelta object
     */
    @JsonProperty("delta")
    public HashMap<String,Object> delta;

    @JsonProperty("ids")
    public List<String> ids = new ArrayList<String>();

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        LedgerStateDeltaForTransactionGroup other = (LedgerStateDeltaForTransactionGroup) o;
        if (!Objects.deepEquals(this.delta, other.delta)) return false;
        if (!Objects.deepEquals(this.ids, other.ids)) return false;

        return true;
    }
}
