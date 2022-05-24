package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.abi.Method;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.TEALProgram;
import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.transaction.MethodCallParams;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.transaction.TxnSigner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@SuppressWarnings("unchecked")
public class MethodCallTransactionBuilder extends TransactionParametersBuilder<MethodCallTransactionBuilder> implements StateSchemaSetter<MethodCallTransactionBuilder>, TEALProgramSetter<MethodCallTransactionBuilder>, ApplicationCallReferencesSetter<MethodCallTransactionBuilder> {
    protected Long appID;
    protected Transaction.OnCompletion onCompletion;
    protected Method method;
    protected List<Object> methodArgs = new ArrayList<>();

    protected List<Address> foreignAccounts = new ArrayList<>();
    protected List<Long> foreignAssets = new ArrayList<>();
    protected List<Long> foreignApps = new ArrayList<>();

    protected TEALProgram approvalProgram, clearStateProgram;
    protected StateSchema localStateSchema;
    protected StateSchema globalStateSchema;
    protected Long extraPages;

    protected TxnSigner signer;

    /**
     * Initialize a {@link MethodCallTransactionBuilder}.
     */
    public static MethodCallTransactionBuilder Builder() {
        return new MethodCallTransactionBuilder();
    }

    @Override
    public MethodCallTransactionBuilder applicationId(Long applicationId) {
        this.appID = applicationId;
        return this;
    }

    /**
     * This is the faux application type used to distinguish different application actions. Specifically, OnCompletion
     * specifies what side effects this transaction will have if it successfully makes it into a block.
     */
    public MethodCallTransactionBuilder onComplete(Transaction.OnCompletion op) {
        this.onCompletion = op;
        return this;
    }

    /**
     * Specify the ABI method that this method call transaction will invoke.
     */
    public MethodCallTransactionBuilder method(Method method) {
        this.method = method;
        return this;
    }

    /**
     * Specify arguments for the ABI method invocation.
     * 
     * This will reset the arguments list to what is passed in by the caller.
     */
    public MethodCallTransactionBuilder methodArguments(List<Object> arguments) {
        this.methodArgs = new ArrayList<>(arguments);
        return this;
    }

    /**
     * Specify arguments for the ABI method invocation.
     * 
     * This will add the arguments passed in by the caller to the existing list of arguments.
     */
    public MethodCallTransactionBuilder addMethodArguments(List<Object> arguments) {
        this.methodArgs.addAll(arguments);
        return this;
    }

    /**
     * Specify arguments for the ABI method invocation.
     * 
     * This will add the argument passed in by the caller to the existing list of arguments.
     */
    public MethodCallTransactionBuilder addMethodArgument(Object argument) {
        this.methodArgs.add(argument);
        return this;
    }

    /**
     * Specify the signer for this method call transaction.
     */
    public MethodCallTransactionBuilder signer(TxnSigner signer) {
        this.signer = signer;
        return this;
    }

    @Override
    public MethodCallTransactionBuilder accounts(List<Address> accounts) {
        if (accounts != null)
            this.foreignAccounts = new ArrayList<>(new HashSet<>(accounts));
        else
            this.foreignAccounts.clear();
        return this;
    }

    @Override
    public MethodCallTransactionBuilder foreignApps(List<Long> foreignApps) {
        if (foreignApps != null)
            this.foreignApps = new ArrayList<>(new HashSet<>(foreignApps));
        else
            this.foreignApps.clear();
        return this;
    }

    @Override
    public MethodCallTransactionBuilder foreignAssets(List<Long> foreignAssets) {
        if (foreignAssets != null)
            this.foreignAssets = new ArrayList<>(new HashSet<>(foreignAssets));
        else
            this.foreignAssets.clear();
        return this;
    }

    @Override
    public MethodCallTransactionBuilder approvalProgram(TEALProgram approvalProgram) {
        this.approvalProgram = approvalProgram;
        return this;
    }

    @Override
    public MethodCallTransactionBuilder clearStateProgram(TEALProgram clearStateProgram) {
        this.clearStateProgram = clearStateProgram;
        return this;
    }

    @Override
    public MethodCallTransactionBuilder localStateSchema(StateSchema localStateSchema) {
        this.localStateSchema = localStateSchema;
        return this;
    }

    @Override
    public MethodCallTransactionBuilder globalStateSchema(StateSchema globalStateSchema) {
        this.globalStateSchema = globalStateSchema;
        return this;
    }

    @Override
    public MethodCallTransactionBuilder extraPages(Long extraPages) {
        if (extraPages == null || extraPages < 0 || extraPages > 3) {
            throw new IllegalArgumentException("extraPages must be an integer between 0 and 3 inclusive");
        }
        this.extraPages = extraPages;
        return this;
    }

    /**
     * Build a MethodCallParams object.
     */
    public MethodCallParams build() {
        return new MethodCallParams(
                appID, method, methodArgs, sender, onCompletion, note, lease, genesisID, genesisHash,
                firstValid, lastValid, fee, flatFee, rekeyTo, signer, foreignAccounts, foreignAssets, foreignApps,
                approvalProgram, clearStateProgram, globalStateSchema, localStateSchema, extraPages
        );
    }
}
