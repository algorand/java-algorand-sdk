package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.transaction.Transaction;

@SuppressWarnings("unchecked")
public class ApplicationCreateTransactionBuilder<T extends  ApplicationCreateTransactionBuilder<T>> extends ApplicationUpdateTransactionBuilder<T> implements StateSchemaSetter<T> {
    private StateSchema localStateSchema;
    private StateSchema globalStateSchema;
    private Long extraPages = 0L;

    /**
     * Initialize a {@link ApplicationCreateTransactionBuilder}.
     */
    public static ApplicationCreateTransactionBuilder<?> Builder() {
        return new ApplicationCreateTransactionBuilder<>();
    }

    public ApplicationCreateTransactionBuilder() {
        super.onCompletion(Transaction.OnCompletion.NoOpOC);
        super.applicationId(0L);
    }

    @Override
    protected void applyTo(Transaction txn) {
        txn.localStateSchema = localStateSchema;
        txn.globalStateSchema = globalStateSchema;
        txn.extraPages = extraPages;

        super.applyTo(txn);
    }

    /**
     * This option is disabled for application create, where the ID must be changed from 0.
     */
    @Override
    public T applicationId(Long appId) {
        if (appId != 0L) {
            throw new IllegalArgumentException("Application ID must be zero, do not set this for application create.");
        }
        return (T) this;
    }

    /**
     * When creating an application, you have the option of opting in with the same transaction. Without this flag a
     * separate transaction is needed to opt-in.
     */
    public T optIn(boolean optIn) {
        if (optIn) {
            super.onCompletion(Transaction.OnCompletion.OptInOC);
        } else {
            super.onCompletion(Transaction.OnCompletion.NoOpOC);
        }
        return (T) this;
    }

    @Override
    public T localStateSchema(StateSchema localStateSchema) {
        this.localStateSchema = localStateSchema;
        return (T) this;
    }

    @Override
    public T globalStateSchema(StateSchema globalStateSchema) {
        this.globalStateSchema = globalStateSchema;
        return (T) this;
    }

    @Override
    public T extraPages(Long extraPages) {
        if (extraPages == null || extraPages < 0 || extraPages > 3) {
            throw new IllegalArgumentException("extraPages must be an integer between 0 and 3 inclusive");
        }
        this.extraPages = extraPages;
        return (T) this;
    }
}
