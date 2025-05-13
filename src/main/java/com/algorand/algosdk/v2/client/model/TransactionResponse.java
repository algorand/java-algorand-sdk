package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class TransactionResponse extends PathResponse {

    /**
     * Round at which the results were computed.
     */
    @JsonProperty("current-round")
    public Long currentRound;

    /**
     * Contains all fields common to all transactions and serves as an envelope to all
     * transactions type. Represents both regular and inner transactions.
     * Definition:
     * data/transactions/signedtxn.go : SignedTxn
     * data/transactions/transaction.go : Transaction
     *
     */
    @JsonProperty("transaction")
    public Transaction transaction;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        TransactionResponse other = (TransactionResponse) o;
        if (!Objects.deepEquals(this.currentRound, other.currentRound)) return false;
        if (!Objects.deepEquals(this.transaction, other.transaction)) return false;

        return true;
    }
}
