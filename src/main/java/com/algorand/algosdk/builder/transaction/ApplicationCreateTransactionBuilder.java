package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.crypto.TEALProgram;
import com.algorand.algosdk.transaction.ApplicationTransactionParams;
import com.algorand.algosdk.transaction.Transaction;

public class ApplicationCreateTransactionBuilder<T extends  ApplicationCreateTransactionBuilder<T>> extends ApplicationBaseTransactionBuilder<T> {
    public TEALProgram approvalProgram;
    public TEALProgram clearStateProgram;

    /**
     * Initialize a {@link ApplicationCreateTransactionBuilder}.
     */
    public static ApplicationCreateTransactionBuilder<?> Builder() {
        return new ApplicationCreateTransactionBuilder<>();
    }

    public ApplicationCreateTransactionBuilder() {
        super();
    }

    @Override
    protected void applyTo(Transaction txn) {
        if (txn.appParams == null) {
            txn.appParams = new ApplicationTransactionParams();
        }

        txn.appParams.clearStateProgram = clearStateProgram;
        txn.appParams.approvalProgram = approvalProgram;

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
