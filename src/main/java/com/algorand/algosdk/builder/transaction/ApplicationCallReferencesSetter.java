package com.algorand.algosdk.builder.transaction;

import java.util.List;

import com.algorand.algosdk.crypto.Address;

public interface ApplicationCallReferencesSetter<T extends ApplicationCallReferencesSetter<T>> {
    
    /**
     * ApplicationID is the application being interacted with, or 0 if creating a new application.
     */
    public T applicationId(Long applicationId);

    /**
     * Accounts lists the accounts (in addition to the sender) that may be accessed from the application logic.
     */
    public T accounts(List<Address> accounts);

    /**
     * ForeignApps lists the applications (in addition to txn.ApplicationID) whose global states may be accessed by this
     * application. The access is read-only.
     */
    public T foreignApps(List<Long> foreignApps);

    /**
     * ForeignAssets lists the assets whose global states may be accessed by this
     * application. The access is read-only.
     */
    public T foreignAssets(List<Long> foreignAssets);
}
