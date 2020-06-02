package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.transaction.ApplicationTransactionParams;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;

import java.util.ArrayList;
import java.util.List;

public class ApplicationBaseTransactionBuilder<T extends ApplicationBaseTransactionBuilder<T>> extends TransactionBuilder<T> {
    public Long applicationId;
    public ApplicationTransactionParams.OnCompletion onCompletion;
    public List<byte[]> applicationArgs;
    public List<Address> accounts;
    public List<Long> foreignApps;
    public StateSchema localStateSchema;
    public StateSchema globalStateSchema;

    /**
     * Initialize a {@link ApplicationCreateTransactionBuilder}.
     */
    public static ApplicationCreateTransactionBuilder<?> Builder() {
        return new ApplicationCreateTransactionBuilder<>();
    }

    protected ApplicationBaseTransactionBuilder() {
        super(Transaction.Type.ApplicaitonCall);
    }

    /**
     * ApplicationID is the application being interacted with, or 0 if creating a new application.
     */
    public T applicationId(Long applicationId) {
        this.applicationId = applicationId;
        return (T) this;
    }

    /**
     * This is the faux application type used to distinguish different application actions. Specifically, OnCompletion
     * specifies what side effects this transaction will have if it successfully makes it into a block.
     */
    public T onCompletion(ApplicationTransactionParams.OnCompletion onCompletion) {
        this.onCompletion = onCompletion;
        return (T) this;
    }

    /**
     * ApplicationArgs lists some transaction-specific arguments accessible from application logic.
     */
    public T args(List<byte[]> args) {
        this.applicationArgs = args;
        return (T) this;
    }

    /**
     * ApplicationArgs lists some transaction-specific arguments accessible from application logic.
     * @param args List of Base64 encoded strings.
     */
    public T argsBase64Encoded(List<String> args) {
        List<byte[]> decodedArgs = new ArrayList<>();
        for (String arg : args) {
            decodedArgs.add(Encoder.decodeFromBase64(arg));
        }
        return this.args(decodedArgs);
    }

    /**
     * Accounts lists the accounts (in addition to the sender) that may be accessed from the application logic.
     */
    public T accounts(List<Address> accounts) {
        this.accounts = accounts;
        return (T) this;
    }

    /**
     * ForeignApps lists the applications (in addition to txn.ApplicationID) whose global states may be accessed by this
     * application. The access is read-only.
     */
    public T foreignApps(List<Long> foreignApps) {
        this.foreignApps = foreignApps;
        return (T) this;
    }

    //public StateSchema localStateSchema;

    /**
     * LocalStateSchema sets limits on the number of strings and integers that may be stored in an account's LocalState.
     * for this application. The larger these limits are, the larger minimum balance must be maintained inside the
     * account of any users who opt into this application. The LocalStateSchema is immutable.
     */
    public T localStateSchema(StateSchema localStateSchema) {
        this.localStateSchema = localStateSchema;
        return (T) this;
    }

    /**
     * GlobalStateSchema sets limits on the number of strings and integers that may be stored in the GlobalState. The
     * larger these limits are, the larger minimum balance must be maintained inside the creator's account (in order to
     * 'pay' for the state that can be used). The GlobalStateSchema is immutable.
     */
    public T globalStateSchema(StateSchema globalStateSchema) {
        this.globalStateSchema = globalStateSchema;
        return (T) this;
    }

    @Override
    protected void applyTo(Transaction txn) {
        ApplicationTransactionParams atp = txn.appParams;
        if (txn.appParams == null) {
            txn.appParams = new ApplicationTransactionParams();
        }

        txn.appParams.applicationId = applicationId;
        txn.appParams.onCompletion = onCompletion;
        txn.appParams.applicationArgs = applicationArgs;
        txn.appParams.accounts = accounts;
        txn.appParams.foreignApps = foreignApps;
        txn.appParams.localStateSchema = localStateSchema;
        txn.appParams.globalStateSchema = globalStateSchema;
    }
}
