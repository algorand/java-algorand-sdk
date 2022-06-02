package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.logic.StateSchema;

public interface StateSchemaSetter<T extends StateSchemaSetter<T>> {

    /**
     * LocalStateSchema sets limits on the number of strings and integers that may be stored in an account's LocalState.
     * for this application. The larger these limits are, the larger minimum balance must be maintained inside the
     * account of any users who opt into this application. The LocalStateSchema is immutable.
     */
    public T localStateSchema(StateSchema localStateSchema);

    /**
     * GlobalStateSchema sets limits on the number of strings and integers that may be stored in the GlobalState. The
     * larger these limits are, the larger minimum balance must be maintained inside the creator's account (in order to
     * 'pay' for the state that can be used). The GlobalStateSchema is immutable.
     */
    public T globalStateSchema(StateSchema globalStateSchema);

    /**
     * extraPages allows you to rent extra pages of memory for the application. Each page is 2048 bytes of shared
     * memory between approval and clear state programs. extraPages parameter must be an integer between 0 and 3 inclusive.
     */
    public T extraPages(Long extraPages);
}
