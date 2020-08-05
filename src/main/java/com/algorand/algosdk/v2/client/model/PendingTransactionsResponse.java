package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A potentially truncated list of transactions currently in the node's transaction
 * pool. You can compute whether or not the list is truncated if the number of
 * elements in the   top-transactions   array is fewer than   total-transactions  .
 */
public class PendingTransactionsResponse extends PathResponse {

    /**
     * An array of signed transaction objects.
     */
    @JsonProperty("top-transactions")
    public List<SignedTransaction> topTransactions = new ArrayList<SignedTransaction>();

    /**
     * Total number of transactions in the pool.
     */
    @JsonProperty("total-transactions")
    public Long totalTransactions;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        PendingTransactionsResponse other = (PendingTransactionsResponse) o;
        if (!Objects.deepEquals(this.topTransactions, other.topTransactions)) return false;
        if (!Objects.deepEquals(this.totalTransactions, other.totalTransactions)) return false;

        return true;
    }
}
