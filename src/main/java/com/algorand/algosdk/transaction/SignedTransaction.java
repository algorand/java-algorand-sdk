package com.algorand.algosdk.transaction;

import com.algorand.algosdk.crypto.MultisigSignature;
import com.algorand.algosdk.crypto.Signature;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/**
 * A serializable convenience type for packaging transactions with their signatures.
 */
@JsonPropertyOrder(alphabetic=true)
public class SignedTransaction {
    @JsonProperty("txn")
    public final Transaction tx;
    @JsonProperty("sig")
    public final Signature sig; // can be null
    @JsonProperty("msig")
    public final MultisigSignature mSig;

    @JsonIgnore
    public final String transactionID;

    public SignedTransaction(Transaction tx, Signature sig, MultisigSignature mSig, String transactionID) {
        this.tx = Objects.requireNonNull(tx, "tx must not be null");
        this.mSig = Objects.requireNonNull(mSig, "mSig must not be null");
        this.sig = Objects.requireNonNull(sig, "sig must not be null");
        this.transactionID = Objects.requireNonNull(transactionID, "txID must not be null");
    }

    public SignedTransaction(Transaction tx, Signature sig, String txId) {
        this(tx, sig, new MultisigSignature(), txId);
    }

    public SignedTransaction(Transaction tx, MultisigSignature mSig, String txId) {
        this(tx, new Signature(), mSig, txId);
    }

    // default constructor for default values to ignore (mirroring msgpack go)
    public SignedTransaction() {
        this.tx = new Transaction();
        this.mSig = new MultisigSignature();
        this.sig = new Signature();
        this.transactionID = "";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SignedTransaction) {
            SignedTransaction actual = (SignedTransaction) obj;
            if (!tx.equals(actual.tx)) return false;
            if (!sig.equals(actual.sig)) return false;
            return this.mSig.equals(actual.mSig);
        } else {
            return false;
        }
    }
}
