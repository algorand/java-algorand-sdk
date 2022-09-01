package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Fields for a state proof transaction.
 * Definition:
 * data/transactions/stateproof.go : StateProofTxnFields
 */
public class TransactionStateProof extends PathResponse {

    /**
     * (spmsg)
     */
    @JsonProperty("message")
    public StateProofMessage message;

    /**
     * (sp) represents a state proof.
     * Definition:
     * crypto/stateproof/structs.go : StateProof
     */
    @JsonProperty("state-proof")
    public StateProof stateProof;

    /**
     * (sptype) Type of the state proof. Integer representing an entry defined in
     * protocol/stateproof.go
     */
    @JsonProperty("state-proof-type")
    public java.math.BigInteger stateProofType;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        TransactionStateProof other = (TransactionStateProof) o;
        if (!Objects.deepEquals(this.message, other.message)) return false;
        if (!Objects.deepEquals(this.stateProof, other.stateProof)) return false;
        if (!Objects.deepEquals(this.stateProofType, other.stateProofType)) return false;

        return true;
    }
}
