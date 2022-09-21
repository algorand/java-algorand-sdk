package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.AppBoxReference;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unchecked")
public abstract class ApplicationBaseTransactionBuilder<T extends ApplicationBaseTransactionBuilder<T>> extends TransactionBuilder<T> implements ApplicationCallReferencesSetter<T> {
    private Transaction.OnCompletion onCompletion;
    private List<byte[]> applicationArgs;
    private List<Address> accounts;
    private List<Long> foreignApps;
    private List<Long> foreignAssets;
    private List<AppBoxReference> appBoxReferences;
    private Long applicationId;

    /**
     * All application calls use this type, so no need to make this private. This constructor should always be called.
     */
    protected ApplicationBaseTransactionBuilder() {
        super(Transaction.Type.ApplicationCall);
    }

    @Override
    protected void applyTo(Transaction txn) {
        // Global requirements
        Objects.requireNonNull(onCompletion, "OnCompletion is required, please file a bug report.");
        Objects.requireNonNull(applicationId);

        if (applicationId != null) txn.applicationId = applicationId;
        if (onCompletion != null) txn.onCompletion = onCompletion;
        if (applicationArgs != null) txn.applicationArgs = applicationArgs;
        if (accounts != null) txn.accounts = accounts;
        if (foreignApps != null) txn.foreignApps = foreignApps;
        if (foreignAssets != null) txn.foreignAssets = foreignAssets;
        if (appBoxReferences != null) txn.boxReferences = convertBoxes(appBoxReferences, foreignApps, applicationId);
    }

    @Override
    public T applicationId(Long applicationId) {
        this.applicationId = applicationId;
        return (T) this;
    }

    /**
     * This is the faux application type used to distinguish different application actions. Specifically, OnCompletion
     * specifies what side effects this transaction will have if it successfully makes it into a block.
     */
    protected T onCompletion(Transaction.OnCompletion onCompletion) {
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
     *
     * @param args List of Base64 encoded strings.
     */
    public T argsBase64Encoded(List<String> args) {
        List<byte[]> decodedArgs = new ArrayList<>();
        for (String arg : args) {
            decodedArgs.add(Encoder.decodeFromBase64(arg));
        }
        return this.args(decodedArgs);
    }

    @Override
    public T accounts(List<Address> accounts) {
        this.accounts = accounts;
        return (T) this;
    }

    @Override
    public T foreignApps(List<Long> foreignApps) {
        this.foreignApps = foreignApps;
        return (T) this;
    }

    @Override
    public T foreignAssets(List<Long> foreignAssets) {
        this.foreignAssets = foreignAssets;
        return (T) this;
    }

    private List<Transaction.BoxReference> convertBoxes(List<AppBoxReference> abrs, List<Long> foreignApps, Long curApp) {
        ArrayList<Transaction.BoxReference> xs = new ArrayList<>();
        for (AppBoxReference abr : abrs) {
            xs.add(Transaction.BoxReference.fromAppBoxReference(abr, foreignApps, curApp));
        }
        return xs;
    }

    public T boxReferences(List<AppBoxReference> boxReferences) {
        this.appBoxReferences = boxReferences;
        return (T) this;
    }
}
