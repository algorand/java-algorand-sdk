package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.crypto.TEALProgram;
import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.transaction.Transaction;

@SuppressWarnings("unchecked")
public class ApplicationUpdateTransactionBuilder<T extends ApplicationUpdateTransactionBuilder<T>> extends ApplicationBaseTransactionBuilder<T> {
    private TEALProgram approvalProgram;
    private TEALProgram clearStateProgram;

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

        super.applyTo(txn);
    }

    /**
     * ApprovalProgram determines whether or not this ApplicationCall transaction will be approved or not.
     */
    public T approvalProgram(TEALProgram approvalProgram) {
        this.approvalProgram = approvalProgram;
        return (T) this;
    }

    /**
     * ClearStateProgram executes when a clear state ApplicationCall transaction is executed. This program may not
     * reject the transaction, only update state.
     */
    public T clearStateProgram(TEALProgram clearStateProgram) {
        this.clearStateProgram = clearStateProgram;
        return (T) this;
    }
}
