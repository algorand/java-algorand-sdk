package com.algorand.algosdk.transaction;

import com.algorand.algosdk.crypto.MultisigSignature;
import com.algorand.algosdk.crypto.Signature;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/**
 * A serializable convenience type for packaging transactions with their signatures.
 */
@JsonPropertyOrder(alphabetic=true)
public class SignedTransaction {
    @JsonProperty("txn")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public final Transaction tx;
    @JsonProperty("sig")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public final Signature sig; // can be null
    @JsonProperty("msig")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public final MultisigSignature mSig;

    @JsonIgnore
    public final String transactionID;

    public SignedTransaction(Transaction tx, Signature sig, MultisigSignature mSig, String transactionID) {
        this.tx = Objects.requireNonNull(tx, "tx must not be null");
        this.mSig = mSig;
        this.sig = sig;
        this.transactionID = transactionID;
    }

    public SignedTransaction(Transaction tx, Signature sig, String txId) {
        this(tx, sig,null, txId);
    }

    public SignedTransaction(Transaction tx, MultisigSignature mSig, String txId) {
        this(tx, null, mSig, txId);
    }
}
