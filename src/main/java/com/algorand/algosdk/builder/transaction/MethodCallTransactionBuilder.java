package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.abi.Method;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.crypto.TEALProgram;
import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.transaction.AppBoxReference;
import com.algorand.algosdk.transaction.MethodCallParams;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.transaction.TxnSigner;

import java.math.BigInteger;
import java.util.ArrayList;
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
    protected List<AppBoxReference> boxReferences = new ArrayList<>();

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
     * <p>
     * This will reset the arguments list to what is passed in by the caller.
     */
    public T methodArguments(List<Object> arguments) {
        this.methodArgs = new ArrayList<>(arguments);
        return (T) this;
    }

    /**
     * Specify arguments for the ABI method invocation.
     * <p>
     * This will add the arguments passed in by the caller to the existing list of arguments.
     */
    public T addMethodArguments(List<Object> arguments) {
        this.methodArgs.addAll(arguments);
        return (T) this;
    }

    /**
     * Specify arguments for the ABI method invocation.
     * <p>
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
            this.foreignAccounts = new ArrayList<>(accounts);
        else
            this.foreignAccounts.clear();
        return (T) this;
    }

    @Override
    public T foreignApps(List<Long> foreignApps) {
        if (foreignApps != null)
            this.foreignApps = new ArrayList<>(foreignApps);
        else
            this.foreignApps.clear();
        return (T) this;
    }

    @Override
    public T foreignAssets(List<Long> foreignAssets) {
        if (foreignAssets != null)
            this.foreignAssets = new ArrayList<>(foreignAssets);
        else
            this.foreignAssets.clear();
        return (T) this;
    }

    @Override
    public T boxReferences(List<AppBoxReference> boxReferences) {
        if (boxReferences != null)
            // duplicate box references can be meaningful, don't get rid of them
            this.boxReferences = new ArrayList<>(boxReferences);
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
        return new MethodCallParamsFactory(appID, method, methodArgs, sender, onCompletion, note, lease, genesisID, genesisHash,
                firstValid, lastValid, fee, flatFee, rekeyTo, signer, foreignAccounts, foreignAssets, foreignApps,
                boxReferences, approvalProgram, clearStateProgram, globalStateSchema, localStateSchema, extraPages);
    }

    /**
     * MethodCallParamsFactory exists only as a way to facilitate construction of
     * `MethodCallParams` instances via a protected constructor.
     * <p>
     * No extension or other modification is intended.
     */
    private static class MethodCallParamsFactory extends MethodCallParams {

        MethodCallParamsFactory(Long appID, Method method, List<Object> methodArgs, Address sender,
                                Transaction.OnCompletion onCompletion, byte[] note, byte[] lease, String genesisID, Digest genesisHash,
                                BigInteger firstValid, BigInteger lastValid, BigInteger fee, BigInteger flatFee,
                                Address rekeyTo, TxnSigner signer,
                                List<Address> fAccounts, List<Long> fAssets, List<Long> fApps, List<AppBoxReference> boxes,
                                TEALProgram approvalProgram, TEALProgram clearProgram,
                                StateSchema globalStateSchema, StateSchema localStateSchema, Long extraPages) {
            super(appID, method, methodArgs, sender,
                    onCompletion, note, lease, genesisID, genesisHash,
                    firstValid, lastValid, fee, flatFee,
                    rekeyTo, signer,
                    fAccounts, fAssets, fApps, boxes,
                    approvalProgram, clearProgram,
                    globalStateSchema, localStateSchema, extraPages);
        }

    }
}
