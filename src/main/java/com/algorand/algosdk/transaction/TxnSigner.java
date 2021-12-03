package com.algorand.algosdk.transaction;

public interface TxnSigner {
    SignedTransaction[] signTxnGroup(Transaction[] txnGroup, int[] indicesToSign) throws Exception;
}
