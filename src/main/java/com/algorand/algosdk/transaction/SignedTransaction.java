package com.algorand.algosdk.transaction;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.crypto.MultisigSignature;
import com.algorand.algosdk.crypto.Signature;
import com.fasterxml.jackson.annotation.*;

import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * A serializable convenience type for packaging transactions with their signatures.
 */
@JsonPropertyOrder(alphabetic=true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class SignedTransaction implements Serializable {
    @JsonProperty("txn")
    public Transaction tx = new Transaction();
    @JsonProperty("sig")
    public Signature sig = new Signature(); // can be null
    @JsonProperty("msig")
    public MultisigSignature mSig = new MultisigSignature();
    @JsonProperty("lsig")
    public LogicsigSignature lSig = new LogicsigSignature();
    @JsonProperty("sgnr")
    public Address authAddr;
    

    @JsonIgnore
    public String transactionID = "";

    public SignedTransaction(
        Transaction tx, Signature sig, MultisigSignature mSig, LogicsigSignature lSig, String transactionID
    ) {
        this.tx = Objects.requireNonNull(tx, "tx must not be null");
        this.mSig = Objects.requireNonNull(mSig, "mSig must not be null");
        this.sig = Objects.requireNonNull(sig, "sig must not be null");
        this.lSig = Objects.requireNonNull(lSig, "lSig must not be null");
        this.transactionID = Objects.requireNonNull(transactionID, "txID must not be null");
    }

    public SignedTransaction(Transaction tx, Signature sig) throws IOException, NoSuchAlgorithmException {
        this(tx, sig, new MultisigSignature(), new LogicsigSignature(), tx.txID());
    }

    public SignedTransaction(Transaction tx, Signature sig, String txId) {
        this(tx, sig, new MultisigSignature(), new LogicsigSignature(), txId);
    }

    public SignedTransaction(Transaction tx, MultisigSignature mSig) throws IOException, NoSuchAlgorithmException {
        this(tx, new Signature(), mSig, new LogicsigSignature(), tx.txID());
    }

    public SignedTransaction(Transaction tx, MultisigSignature mSig, String txId) {
        this(tx, new Signature(), mSig, new LogicsigSignature(), txId);
    }

    public SignedTransaction(Transaction tx, LogicsigSignature lSig) throws IOException, NoSuchAlgorithmException {
        this(tx, new Signature(), new MultisigSignature(), lSig, tx.txID());
    }

    public SignedTransaction(Transaction tx, LogicsigSignature lSig, String txId) {
        this(tx, new Signature(), new MultisigSignature(), lSig, txId);
    }
    
    public SignedTransaction authAddr(Address authAddr) {
    	this.authAddr = authAddr;
    	return this;
    }

    @JsonCreator
    public SignedTransaction(
            @JsonProperty("txn") Transaction tx,
            @JsonProperty("sig") byte[] sig,
            @JsonProperty("msig") MultisigSignature mSig,
            @JsonProperty("lsig") LogicsigSignature lSig
    ) {
        if (tx != null) this.tx = tx;
        if (sig != null) this.sig = new Signature(sig);
        if (mSig != null) this.mSig = mSig;
        if (lSig != null) this.lSig = lSig;
        // don't recover the txid yet
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SignedTransaction) {
            SignedTransaction actual = (SignedTransaction) obj;
            if (!tx.equals(actual.tx)) return false;
            if (!sig.equals(actual.sig)) return false;
            if (!lSig.equals(actual.lSig)) return false;
            if (!authAddr.equals(actual.authAddr)) return false;
            return this.mSig.equals(actual.mSig);
        } else {
            return false;
        }
    }
}
