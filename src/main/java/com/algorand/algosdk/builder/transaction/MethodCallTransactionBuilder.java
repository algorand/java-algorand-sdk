package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.abi.Method;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.TEALProgram;
import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.transaction.BoxReference;
import com.algorand.algosdk.transaction.MethodCallParams;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.transaction.TxnSigner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@SuppressWarnings("unchecked")
public class MethodCallTransactionBuilder<T extends MethodCallTransactionBuilder<T>> extends TransactionParametersBuilder<T> implements StateSchemaSetter<T>, TEALProgramSetter<T>, ApplicationCallReferencesSetter<T> {
    protected Long appID;
    protected Transaction.OnCompletion onCompletion;
    protected Method method;
    protected List<Object> methodArgs = new ArrayList<>();

    protected List<Address> foreignAccounts = new ArrayList<>();
    protected List<Long> foreignAssets = new ArrayList<>();
    protected List<Long> foreignApps = new ArrayList<>();
    protected List<BoxReference> boxReferences = new ArrayList<>();

    protected TEALProgram approvalProgram, clearStateProgram;
    protected StateSchema localStateSchema;
    protected StateSchema globalStateSchema;
    protected Long extraPages;

    protected TxnSigner signer;

    /**
     * Initialize a {@link MethodCallTransactionBuilder}.
     */
    public static MethodCallTransactionBuilder<?> Builder() {
        return new MethodCallTransactionBuilder<>();
    }

    @Override
    public T applicationId(Long applicationId) {
        this.appID = applicationId;
        return (T) this;
    }

    /**
     * This is the faux application type used to distinguish different application actions. Specifically, OnCompletion
     * specifies what side effects this transaction will have if it successfully makes it into a block.
     */
    public T onComplete(Transaction.OnCompletion op) {
        this.onCompletion = op;
        return (T) this;
    }

    /**
     * Specify the ABI method that this method call transaction will invoke.
     */
    public T method(Method method) {
        this.method = method;
        return (T) this;
    }

    /**
     * Specify arguments for the ABI method invocation.
     * 
     * This will reset the arguments list to what is passed in by the caller.
     */
    public T methodArguments(List<Object> arguments) {
        this.methodArgs = new ArrayList<>(arguments);
        return (T) this;
    }

    /**
     * Specify arguments for the ABI method invocation.
     * 
     * This will add the arguments passed in by the caller to the existing list of arguments.
     */
    public T addMethodArguments(List<Object> arguments) {
        this.methodArgs.addAll(arguments);
        return (T) this;
    }

    /**
     * Specify arguments for the ABI method invocation.
     * 
     * This will add the argument passed in by the caller to the existing list of arguments.
     */
    public T addMethodArgument(Object argument) {
        this.methodArgs.add(argument);
        return (T) this;
    }

    /**
     * Specify the signer for this method call transaction.
     */
    public T signer(TxnSigner signer) {
        this.signer = signer;
        return (T) this;
    }

    @Override
    public T accounts(List<Address> accounts) {
        if (accounts != null)
            this.foreignAccounts = new ArrayList<>(new HashSet<>(accounts));
        else
            this.foreignAccounts.clear();
        return (T) this;
    }

    @Override
    public T foreignApps(List<Long> foreignApps) {
        if (foreignApps != null)
            this.foreignApps = new ArrayList<>(new HashSet<>(foreignApps));
        else
            this.foreignApps.clear();
        return (T) this;
    }

    @Override
    public T foreignAssets(List<Long> foreignAssets) {
        if (foreignAssets != null)
            this.foreignAssets = new ArrayList<>(new HashSet<>(foreignAssets));
        else
            this.foreignAssets.clear();
        return (T) this;
    }

    @Override
    public T boxReferences(List<BoxReference> boxReferences) {
        if (boxReferences != null) 
            this.boxReferences = new ArrayList<>(new HashSet<>(boxReferences));
        else
            this.boxReferences.clear();
        return (T) this;
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

    @Override
    public T localStateSchema(StateSchema localStateSchema) {
        this.localStateSchema = localStateSchema;
        return (T) this;
    }

    @Override
    public T globalStateSchema(StateSchema globalStateSchema) {
        this.globalStateSchema = globalStateSchema;
        return (T) this;
    }

    @Override
    public T extraPages(Long extraPages) {
        if (extraPages == null || extraPages < 0 || extraPages > 3) {
            throw new IllegalArgumentException("extraPages must be an integer between 0 and 3 inclusive");
        }
        this.extraPages = extraPages;
        return (T) this;
    }

    /**
     * Build a MethodCallParams object.
     */
    public MethodCallParams build() {
        return new MethodCallParams(
                appID, method, methodArgs, sender, onCompletion, note, lease, genesisID, genesisHash,
                firstValid, lastValid, fee, flatFee, rekeyTo, signer, foreignAccounts, foreignAssets, foreignApps,
                boxReferences, approvalProgram, clearStateProgram, globalStateSchema, localStateSchema, extraPages
        );
    }
}
