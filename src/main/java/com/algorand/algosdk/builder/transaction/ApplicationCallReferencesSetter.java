package com.algorand.algosdk.builder.transaction;

import java.util.List;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.AppBoxReference;

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

    /**
     * BoxReferences lists the boxes whose state may be accessed during evaluation of this application call. The apps
     * the boxes belong to must be present in ForeignApps.
     */
    public T boxReferences(List<AppBoxReference> boxReferences);
}
