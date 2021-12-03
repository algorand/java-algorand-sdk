package com.algorand.algosdk.transaction;

public class TransactionWithSigner {
    public Transaction txn;
    public TxnSigner signer;

    public TransactionWithSigner(Transaction txn, TxnSigner signer) {
        this.txn = txn;
        this.signer = signer;
    }
}
