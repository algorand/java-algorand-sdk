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
     * 'pay' for the state that can be used). The GlobalStateSchema may also be changed during an application update.
     *
     * Note: on an update, a non-zero global state schema or extraPages installs both sizes and zeroes the one
     * left out, so pass the current value of a size that should not change. Leaving both out keeps the current
     * sizes.
     */
    public T globalStateSchema(StateSchema globalStateSchema);

    /**
     * extraPages allows you to rent extra program pages for the application. Each extra page grants 2048 extra
     * bytes of program size available to the approval and clear state programs. extraPages may also be changed during an application update.
     * It must be a non-negative integer; the maximum (currently 7) is enforced by the network.
     *
     * Note: on an update, a non-zero global state schema or extraPages installs both sizes and zeroes the one
     * left out, so pass the current value of a size that should not change. Leaving both out keeps the current
     * sizes.
     */
    public T extraPages(Long extraPages);
}
