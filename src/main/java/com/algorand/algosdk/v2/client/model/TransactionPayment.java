package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Fields for a payment transaction.
 * Definition:
 * data/transactions/payment.go : PaymentTxnFields
 */
public class TransactionPayment extends PathResponse {

    /**
     * (amt) number of MicroAlgos intended to be transferred.
     */
    @JsonProperty("amount")
    public Long amount;

    /**
     * Number of MicroAlgos that were sent to the close-remainder-to address when
     * closing the sender account.
     */
    @JsonProperty("close-amount")
    public Long closeAmount;

    /**
     * (close) when set, indicates that the sending account should be closed and all
     * remaining funds be transferred to this address.
     */
    @JsonProperty("close-remainder-to")
    public String closeRemainderTo;

    /**
     * (rcv) receiver's address.
     */
    @JsonProperty("receiver")
    public String receiver;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        TransactionPayment other = (TransactionPayment) o;
        if (!Objects.deepEquals(this.amount, other.amount)) return false;
        if (!Objects.deepEquals(this.closeAmount, other.closeAmount)) return false;
        if (!Objects.deepEquals(this.closeRemainderTo, other.closeRemainderTo)) return false;
        if (!Objects.deepEquals(this.receiver, other.receiver)) return false;

        return true;
    }
}
