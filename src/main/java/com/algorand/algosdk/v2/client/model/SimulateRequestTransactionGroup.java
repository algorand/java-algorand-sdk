package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A transaction group to simulate.
 */
public class SimulateRequestTransactionGroup extends PathResponse {

    /**
     * An atomic transaction group.
     */
    @JsonProperty("txns")
    public List<SignedTransaction> txns = new ArrayList<SignedTransaction>();

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        SimulateRequestTransactionGroup other = (SimulateRequestTransactionGroup) o;
        if (!Objects.deepEquals(this.txns, other.txns)) return false;

        return true;
    }
}
