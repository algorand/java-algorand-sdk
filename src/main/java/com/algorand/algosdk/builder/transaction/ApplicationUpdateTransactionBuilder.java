package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.crypto.TEALProgram;
import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.transaction.Transaction;

@SuppressWarnings("unchecked")
public class ApplicationUpdateTransactionBuilder<T extends ApplicationUpdateTransactionBuilder<T>> extends ApplicationBaseTransactionBuilder<T> implements TEALProgramSetter<T> {
    private TEALProgram approvalProgram;
    private TEALProgram clearStateProgram;
    private StateSchema globalStateSchema;
    private Long extraPages;

    /**
     * Initialize a {@link ApplicationUpdateTransactionBuilder}.
     */
    public static ApplicationUpdateTransactionBuilder<?> Builder() {
        return new ApplicationUpdateTransactionBuilder<>();
    }

    protected ApplicationUpdateTransactionBuilder() {
        super.onCompletion(Transaction.OnCompletion.UpdateApplicationOC);
    }

    @Override
    protected void applyTo(Transaction txn) {
        txn.clearStateProgram = clearStateProgram;
        txn.approvalProgram = approvalProgram;
        if (globalStateSchema != null) txn.globalStateSchema = globalStateSchema;
        if (extraPages != null) txn.extraPages = extraPages;

        super.applyTo(txn);
    }

    @Override
    public T approvalProgram(TEALProgram approvalProgram) {
        this.approvalProgram = approvalProgram;
        return (T) this;
    }

    @Override
    public T clearStateProgram(TEALProgram clearStateProgram) {
        this.clearStateProgram = clearStateProgram;
        return (T) this;
    }

    /**
     * GlobalStateSchema sets limits on the number of strings and integers that may be stored in the GlobalState. The
     * larger these limits are, the larger minimum balance must be maintained inside the creator's account (in order to
     * 'pay' for the state that can be used). The global state schema may also be changed during an application update.
     * The local state schema cannot be changed after creation.
     *
     * Note: on an update, a non-zero global state schema or extraPages installs both sizes and zeroes the one left
     * out, so pass the current value of a size that should not change. Leaving both out keeps the current sizes.
     *
     * Passing null means unset: the transaction carries no schema (extraPages(null), by contrast, is rejected).
     */
    public T globalStateSchema(StateSchema globalStateSchema) {
        this.globalStateSchema = globalStateSchema;
        return (T) this;
    }

    /**
     * extraPages allows you to rent extra program pages for the application. Each extra page grants 2048 extra
     * bytes of program size available to the approval and clear state programs. extraPages may also be changed during an application update.
     * It must be a non-negative integer; the maximum (currently 7) is enforced by the network.
     *
     * Note: on an update, a non-zero global state schema or extraPages installs both sizes and zeroes the one left
     * out, so pass the current value of a size that should not change. Leaving both out keeps the current sizes.
     */
    public T extraPages(Long extraPages) {
        if (extraPages == null || extraPages < 0) {
            throw new IllegalArgumentException("extraPages must be a non-negative integer");
        }
        this.extraPages = extraPages;
        return (T) this;
    }
}
