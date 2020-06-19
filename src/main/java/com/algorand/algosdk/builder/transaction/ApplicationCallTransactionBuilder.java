package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.transaction.Transaction;

@SuppressWarnings("unchecked")
public class ApplicationCallTransactionBuilder<T extends ApplicationCallTransactionBuilder<T>> extends ApplicationBaseTransactionBuilder<T> {

    /**
     * Initialize a {@link ApplicationCallTransactionBuilder}.
     */
    public static ApplicationCallTransactionBuilder<?> Builder() {
        return new ApplicationCallTransactionBuilder<>();
    }

    protected ApplicationCallTransactionBuilder() {
        this.onCompletion(Transaction.OnCompletion.NoOpOC);
    }
}
