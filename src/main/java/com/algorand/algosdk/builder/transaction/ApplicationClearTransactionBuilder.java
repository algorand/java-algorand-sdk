package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.transaction.Transaction;

@SuppressWarnings("unchecked")
public class ApplicationClearTransactionBuilder<T extends ApplicationClearTransactionBuilder<T>> extends ApplicationBaseTransactionBuilder<T> {

    /**
     * Initialize a {@link ApplicationClearTransactionBuilder}.
     */
    public static ApplicationClearTransactionBuilder<?> Builder() {
        return new ApplicationClearTransactionBuilder<>();
    }

    protected ApplicationClearTransactionBuilder() {
        this.onCompletion(Transaction.OnCompletion.ClearStateOC);
    }
}
