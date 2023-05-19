package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response containing all ledger state deltas for transaction groups, with their
 * associated Ids, in a single round.
 */
public class TransactionGroupLedgerStateDeltasForRoundResponse extends PathResponse {

    @JsonProperty("Deltas")
    public List<LedgerStateDeltaForTransactionGroup> deltas = new ArrayList<LedgerStateDeltaForTransactionGroup>();

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        TransactionGroupLedgerStateDeltasForRoundResponse other = (TransactionGroupLedgerStateDeltasForRoundResponse) o;
        if (!Objects.deepEquals(this.deltas, other.deltas)) return false;

        return true;
    }
}
