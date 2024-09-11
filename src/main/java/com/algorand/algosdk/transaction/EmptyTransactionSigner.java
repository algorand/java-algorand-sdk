package com.algorand.algosdk.transaction;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.util.Encoder;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class EmptyTransactionSigner implements TxnSigner {

    private String authAddr = "";
    /**
     * EmptyTransactionSigner is a TransactionSigner that produces signed transaction objects without
     * signatures. This is useful for simulating transactions, but it won't work for actual submission.
     */
    public EmptyTransactionSigner(String authAddr) {
        super();
        this.authAddr = authAddr;
    }

    /**
     * SignTransactions returns SignedTxn bytes but does not sign them.
     *
     * @param txnGroup      The group of transactions to be signed.
     * @param indicesToSign The indexes of the transactions to sign.
     * @return A list of signed transaction bytes.
     */
    @Override
    public SignedTransaction[] signTxnGroup(Transaction[] txnGroup, int[] indicesToSign) throws NoSuchAlgorithmException {
        SignedTransaction[] stxs = new SignedTransaction[indicesToSign.length];

        for (int pos : indicesToSign) {
            SignedTransaction stx = new SignedTransaction(txnGroup[pos]);

            if (authAddr != null) {
                Address address = new Address(authAddr);
                stx.authAddr(address.getBytes());
            }

            stxs[pos] = stx;
        }
        return stxs;
    }

    /**
     * Equals returns true if the other TransactionSigner equals this one.
     *
     * @param other The other TransactionSigner to compare.
     * @return true if equal, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof EmptyTransactionSigner;
    }

    @Override
    public int hashCode() {
        return Objects.hash(EmptyTransactionSigner.class);
    }
}
