package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.transaction.Transaction;

@SuppressWarnings("unchecked")
public class ApplicationCreateTransactionBuilder<T extends  ApplicationCreateTransactionBuilder<T>> extends ApplicationUpdateTransactionBuilder<T> implements StateSchemaSetter<T> {
    private StateSchema localStateSchema;

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
        // Only assign when set: overwriting the field default with null would
        // serialize "apls" as msgpack nil instead of omitting it
        if (localStateSchema != null) txn.localStateSchema = localStateSchema;

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

    /**
     * GlobalStateSchema sets limits on the number of strings and integers that may be stored in the GlobalState. The
     * larger these limits are, the larger minimum balance must be maintained inside the creator's account (in order to
     * 'pay' for the state that can be used). The global state schema set at creation may later be changed by an
     * application update.
     */
    @Override
    public T globalStateSchema(StateSchema globalStateSchema) {
        return super.globalStateSchema(globalStateSchema);
    }

    /**
     * extraPages allows you to rent extra program pages for the application. Each extra page grants 2048 extra
     * bytes of program size available to the approval and clear state programs. The value set at creation may later be changed by an
     * application update. It must be a non-negative integer; the maximum (currently 7) is enforced by the network.
     */
    @Override
    public T extraPages(Long extraPages) {
        return super.extraPages(extraPages);
    }
}
