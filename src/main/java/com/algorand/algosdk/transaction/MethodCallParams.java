package com.algorand.algosdk.transaction;

import com.algorand.algosdk.abi.Method;
import com.algorand.algosdk.algod.client.model.TransactionParams;
import com.algorand.algosdk.builder.transaction.MethodCallTransactionBuilder;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.crypto.TEALProgram;
import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * MethodCallParams is an object that holds all parameters necessary to invoke {@link AtomicTransactionComposer#addMethodCall(MethodCallParams)}
 */
public class MethodCallParams {
    public final Long appID;
    public final Transaction.OnCompletion onCompletion;
    public final Method method;
    public final List<Object> methodArgs;

    public final List<Address> foreignAccounts;
    public final List<Long> foreignAssets;
    public final List<Long> foreignApps;
    
    public final TEALProgram approvalProgram, clearProgram;
    public final StateSchema globalStateSchema, localStateSchema;
    public final Long extraPages;

    public final TxnSigner signer;

    // from com.algorand.algosdk.builder.transaction.TransactionParametersBuilder
    public final Address sender;
    public final BigInteger fee;
    public final BigInteger flatFee;
    public final BigInteger firstValid;
    public final BigInteger lastValid;
    public final byte[] note;
    public final byte[] lease;
    public final Address rekeyTo;
    public final String genesisID;
    public final Digest genesisHash;

    public MethodCallParams(Long appID, Method method, List<Object> methodArgs, Address sender,
                            Transaction.OnCompletion onCompletion, byte[] note, byte[] lease, String genesisID, Digest genesisHash,
                            BigInteger firstValid, BigInteger lastValid, BigInteger fee, BigInteger flatFee,
                            Address rekeyTo, TxnSigner signer,
                            List<Address> fAccounts, List<Long> fAssets, List<Long> fApps,
                            TEALProgram approvalProgram, TEALProgram clearProgram,
                            StateSchema globalStateSchema, StateSchema localStateSchema, Long extraPages) {
        if (appID == null || method == null || sender == null || onCompletion == null || signer == null || genesisID == null || genesisHash == null || firstValid == null || lastValid == null || (fee == null && flatFee == null))
            throw new IllegalArgumentException("Method call builder error: some required field not added");
        if (fee != null && flatFee != null)
            throw new IllegalArgumentException("Cannot set both fee and flatFee");
        if (method.args.size() != methodArgs.size())
            throw new IllegalArgumentException("Method call error: incorrect method arg number provided");
        if (appID == 0) {
            if (approvalProgram == null || clearProgram == null || globalStateSchema == null || localStateSchema == null)
                throw new IllegalArgumentException(
                        "One of the following required parameters for application creation is missing: " +
                                "approvalProgram, clearProgram, globalStateSchema, localStateSchema"
                );
        } else if (onCompletion == Transaction.OnCompletion.UpdateApplicationOC) {
            if (approvalProgram == null || clearProgram == null)
                throw new IllegalArgumentException(
                        "One of the following required parameters for OnApplicationComplete.UpdateApplicationOC is missing: approvalProgram, clearProgram"
                );
            if (globalStateSchema != null || localStateSchema != null || extraPages != null)
                throw new IllegalArgumentException(
                        "One of the following application creation parameters were set on a non-creation call: " +
                                "globalStateSchema, localStateSchema, extraPages"
                );
        } else {
            if (approvalProgram != null || clearProgram != null || globalStateSchema != null || localStateSchema != null || extraPages != null) {
                throw new IllegalArgumentException(
                        "One of the following application creation parameters were set on a non-creation call: " +
                                "approvalProgram, clearProgram, globalStateSchema, localStateSchema, extraPages"
                );
            }
        }
        this.appID = appID;
        this.method = method;
        this.methodArgs = new ArrayList<>(methodArgs);
        this.sender = sender;
        this.onCompletion = onCompletion;
        this.note = note;
        this.lease = lease;
        this.genesisID = genesisID;
        this.genesisHash = genesisHash;
        this.firstValid = firstValid;
        this.lastValid = lastValid;
        this.fee = fee;
        this.flatFee = flatFee;
        this.rekeyTo = rekeyTo;
        this.signer = signer;
        this.foreignAccounts = new ArrayList<>(fAccounts);
        this.foreignAssets = new ArrayList<>(fAssets);
        this.foreignApps = new ArrayList<>(fApps);
        this.approvalProgram = approvalProgram;
        this.clearProgram = clearProgram;
        this.globalStateSchema = globalStateSchema;
        this.localStateSchema = localStateSchema;
        this.extraPages = extraPages;
    }

    /**
     * Deprecated, use {@link com.algorand.algosdk.builder.transaction.MethodCallTransactionBuilder#Builder()} instead.
     */
    @Deprecated
    public static class Builder extends MethodCallTransactionBuilder<Builder> {

        public Builder setAppID(Long appID) {
            return this.applicationId(appID);
        }

        public Builder setMethod(Method method) {
            return this.method(method);
        }

        public Builder addMethodArgs(Object ma) {
            return this.addMethodArgument(ma);
        }

        public Builder setSender(String sender) {
            return this.sender(sender);
        }

        public Builder setSuggestedParams(TransactionParams sp) {
            return this.suggestedParams(sp);
        }

        public Builder setSuggestedParams(TransactionParametersResponse sp) {
            return this.suggestedParams(sp);
        }

        public Builder setOnComplete(Transaction.OnCompletion op) {
            return this.onComplete(op);
        }

        public Builder setNote(byte[] note) {
            return this.note(note);
        }

        public Builder setLease(byte[] lease) {
            return this.lease(lease);
        }

        public Builder setRekeyTo(String rekeyTo) {
            return this.rekey(rekeyTo);
        }

        public Builder setSigner(TxnSigner signer) {
            return this.signer(signer);
        }

        public Builder setFirstValid(BigInteger fv) {
            return this.firstValid(fv);
        }

        public Builder setLastValid(BigInteger lv) {
            return this.lastValid(lv);
        }

        public Builder setFee(BigInteger fee) {
            return this.fee(fee);
        }

        public Builder setFlatFee(BigInteger flatFee) {
            return this.flatFee(flatFee);
        }

        public Builder setForeignAccounts(List<Address> fAccounts) {
            return this.accounts(fAccounts);
        }

        public Builder setForeignAssets(List<Long> fAssets) {
            return this.foreignAssets(fAssets);
        }

        public Builder setForeignApps(List<Long> fApps) {
            return this.foreignApps(fApps);
        }

        public Builder setApprovalProgram(TEALProgram approvalProgram) {
            return this.approvalProgram(approvalProgram);
        }

        public Builder setClearProgram(TEALProgram clearProgram) {
            return this.clearStateProgram(clearProgram);
        }

        public Builder setGlobalInts(Long globalInts) {
            if (this.globalStateSchema == null) {
                return this.globalStateSchema(new StateSchema(globalInts, 0L));
            }
            this.globalStateSchema.numUint = BigInteger.valueOf(globalInts);
            return this;
        }

        public Builder setGlobalBytes(Long globalBytes) {
            if (this.globalStateSchema == null) {
                return this.globalStateSchema(new StateSchema(0L, globalBytes));
            }
            this.globalStateSchema.numByteSlice = BigInteger.valueOf(globalBytes);
            return this;
        }

        public Builder setLocalInts(Long localInts) {
            if (this.localStateSchema == null) {
                return this.localStateSchema(new StateSchema(localInts, 0L));
            }
            this.localStateSchema.numUint = BigInteger.valueOf(localInts);
            return this;
        }

        public Builder setLocalBytes(Long localBytes) {
            if (this.localStateSchema == null) {
                return this.localStateSchema(new StateSchema(0L, localBytes));
            }
            this.localStateSchema.numByteSlice = BigInteger.valueOf(localBytes);
            return this;
        }

        public Builder setExtraPages(Long extraPages) {
            return this.extraPages(extraPages);
        }
    }
}
