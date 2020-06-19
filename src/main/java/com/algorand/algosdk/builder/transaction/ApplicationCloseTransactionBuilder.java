package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.transaction.Transaction;

@SuppressWarnings("unchecked")
public class ApplicationCloseTransactionBuilder<T extends ApplicationCloseTransactionBuilder<T>> extends ApplicationBaseTransactionBuilder<T> {

    /**
     * Initialize a {@link ApplicationCloseTransactionBuilder}.
     */
    public static ApplicationCloseTransactionBuilder<?> Builder() {
        return new ApplicationCloseTransactionBuilder<>();
    }

    protected ApplicationCloseTransactionBuilder() {
        this.onCompletion(Transaction.OnCompletion.CloseOutOC);
    }
}
