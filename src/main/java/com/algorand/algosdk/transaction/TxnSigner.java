package com.algorand.algosdk.transaction;

public interface TxnSigner {
    SignedTransaction[] signTxnGroup(Transaction[] txnGroup, int[] indicesToSign) throws Exception;

    /**
     * Sign a single transaction with this signer.
     *
     * A convenience wrapper over {@link #signTxnGroup} for the common
     * single-transaction case; the signer-based counterpart of
     * {@link com.algorand.algosdk.account.Account#signTransaction}.
     * @param txn the transaction to sign
     * @return the signed transaction
     */
    default SignedTransaction signTransaction(Transaction txn) throws Exception {
        return signTxnGroup(new Transaction[]{txn}, new int[]{0})[0];
    }
}
