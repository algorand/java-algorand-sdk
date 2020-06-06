package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.transaction.Transaction;

@SuppressWarnings("unchecked")
public class ApplicationCreateTransactionBuilder<T extends  ApplicationCreateTransactionBuilder<T>> extends ApplicationUpdateTransactionBuilder<T> {
    private StateSchema localStateSchema;
    private StateSchema globalStateSchema;

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

    /**
     * LocalStateSchema sets limits on the number of strings and integers that may be stored in an account's LocalState.
     * for this application. The larger these limits are, the larger minimum balance must be maintained inside the
     * account of any users who opt into this application. The LocalStateSchema is immutable.
     */
    public T localStateSchema(StateSchema localStateSchema) {
        this.localStateSchema = localStateSchema;
        return (T) this;
    }

    /**
     * GlobalStateSchema sets limits on the number of strings and integers that may be stored in the GlobalState. The
     * larger these limits are, the larger minimum balance must be maintained inside the creator's account (in order to
     * 'pay' for the state that can be used). The GlobalStateSchema is immutable.
     */
    public T globalStateSchema(StateSchema globalStateSchema) {
        this.globalStateSchema = globalStateSchema;
        return (T) this;
    }
}
