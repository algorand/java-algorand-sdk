package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.transaction.Transaction;

@SuppressWarnings("unchecked")
public class ApplicationDeleteTransactionBuilder<T extends ApplicationDeleteTransactionBuilder<T>> extends ApplicationBaseTransactionBuilder<T> {

    /**
     * Initialize a {@link ApplicationDeleteTransactionBuilder}.
     */
    public static ApplicationDeleteTransactionBuilder<?> Builder() {
        return new ApplicationDeleteTransactionBuilder<>();
    }

    public ApplicationDeleteTransactionBuilder() {
        super.onCompletion(Transaction.OnCompletion.DeleteApplicationOC);
    }
}
