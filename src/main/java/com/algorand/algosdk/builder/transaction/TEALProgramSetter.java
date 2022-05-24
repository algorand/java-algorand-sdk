package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.crypto.TEALProgram;

public interface TEALProgramSetter<T extends TEALProgramSetter<T>> {
    
    /**
     * ApprovalProgram determines whether or not this ApplicationCall transaction will be approved or not.
     */
    public T approvalProgram(TEALProgram approvalProgram);

    /**
     * ClearStateProgram executes when a clear state ApplicationCall transaction is executed. This program may not
     * reject the transaction, only update state.
     */
    public T clearStateProgram(TEALProgram clearStateProgram);
}
