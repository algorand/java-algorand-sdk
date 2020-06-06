package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.transaction.Transaction;

@SuppressWarnings("unchecked")
public class ApplicationOptInTransactionBuilder<T extends ApplicationOptInTransactionBuilder<T>> extends ApplicationBaseTransactionBuilder<T> {

    /**
     * Initialize a {@link ApplicationOptInTransactionBuilder}.
     */
    public static ApplicationOptInTransactionBuilder<?> Builder() {
        return new ApplicationOptInTransactionBuilder<>();
    }

    public ApplicationOptInTransactionBuilder() {
        super.onCompletion(Transaction.OnCompletion.OptInOC);
    }
}
