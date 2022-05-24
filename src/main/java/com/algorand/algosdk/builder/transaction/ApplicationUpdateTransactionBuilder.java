package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.crypto.TEALProgram;
import com.algorand.algosdk.transaction.Transaction;

@SuppressWarnings("unchecked")
public class ApplicationUpdateTransactionBuilder<T extends ApplicationUpdateTransactionBuilder<T>> extends ApplicationBaseTransactionBuilder<T> implements TEALProgramSetter<T> {
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
}
