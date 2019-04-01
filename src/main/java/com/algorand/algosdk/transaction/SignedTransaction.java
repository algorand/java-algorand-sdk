package com.algorand.algosdk.transaction;

import com.algorand.algosdk.crypto.MultisigSignature;
import com.algorand.algosdk.crypto.Signature;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * A serializable convenience type for packaging transactions with their signatures.
 */
public class SignedTransaction {
    @JsonProperty("txn")
    public final Transaction tx;
    @JsonProperty("sig")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public final Signature sig; // can be null
    @JsonProperty("msig")
    public final MultisigSignature mSig; // cannot be null due to the way we serialize in go-codec

    public SignedTransaction(Transaction tx, Signature sig, MultisigSignature mSig) {
        this.tx = Objects.requireNonNull(tx, "tx must not be null");
        this.mSig = Objects.requireNonNull(mSig, "mSig must not be null");
        this.sig = sig;
    }

    public SignedTransaction(Transaction tx, Signature sig) {
        this(tx, sig, new MultisigSignature(0, 0, null));
    }

    public SignedTransaction(Transaction tx, MultisigSignature mSig) {
        this(tx, null, mSig);
    }
}
