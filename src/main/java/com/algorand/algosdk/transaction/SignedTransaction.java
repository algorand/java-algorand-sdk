package com.algorand.algosdk.transaction;

import com.algorand.algosdk.crypto.MultisigSignature;
import com.algorand.algosdk.crypto.Signature;

import java.util.Objects;

/**
 * A serializable convenience type for packaging transactions with their signatures.
 */
public class SignedTransaction {
    public final Transaction tx;
    public final Signature sig;
    public final MultisigSignature mSig;

    public SignedTransaction(Transaction tx, Signature sig, MultisigSignature mSig) {
        this.tx = Objects.requireNonNull(tx, "tx must not be null");
        this.sig = Objects.requireNonNull(sig, "sig must not be null");
        this.mSig = Objects.requireNonNull(mSig, "mSig must not be null");
    }
}
