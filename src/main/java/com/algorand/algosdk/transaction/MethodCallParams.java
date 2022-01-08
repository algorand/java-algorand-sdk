package com.algorand.algosdk.transaction;

import com.algorand.algosdk.abi.Method;
import com.algorand.algosdk.algod.client.model.TransactionParams;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.TEALProgram;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MethodCallParams {
    public Long appID;
    public Method method;
    public List<Object> methodArgs;
    public String sender, rekeyTo;
    public Transaction.OnCompletion onCompletion;
    public byte[] note, lease;
    public BigInteger fv, lv, fee, flatFee;
    TxnSigner signer;
    public TransactionParams suggestedParams;
    public List<Address> foreignAccounts;
    public List<Long> foreignAssets;
    public List<Long> foreignApps;

    public TEALProgram approvalProgram, clearProgram;
    public Long globalInts, globalBytes;
    public Long localInts, localBytes;
    public Long extraPages;

    public MethodCallParams(Long appID, Method method, List<Object> methodArgs, String sender,
                            TransactionParams sp, Transaction.OnCompletion onCompletion, byte[] note, byte[] lease,
                            BigInteger fv, BigInteger lv, BigInteger fee, BigInteger flatFee,
                            String rekeyTo, TxnSigner signer,
                            List<Address> fAccounts, List<Long> fAssets, List<Long> fApps,
                            TEALProgram approvalProgram, TEALProgram clearProgram, Long globalInts, Long globalBytes,
                            Long localInts, Long localBytes, Long extraPages) {
        if (appID == null || method == null || sender == null || onCompletion == null || signer == null || sp == null)
            throw new IllegalArgumentException("Method call builder error: some required field not added");
        if (method.args.size() != methodArgs.size())
            throw new IllegalArgumentException("Method call error: incorrect method arg number provided");
        if (appID == 0) {
            if (approvalProgram == null || clearProgram == null || globalInts == null || localInts == null || globalBytes == null || localBytes == null)
                throw new IllegalArgumentException(
                        "One of the following required parameters for application creation is missing: " +
                                "approvalProgram, clearProgram, numGlobalInts, numGlobalByteSlices, numLocalInts, numLocalByteSlice"
                );
        } else if (onCompletion == Transaction.OnCompletion.UpdateApplicationOC) {
            if (approvalProgram == null || clearProgram == null)
                throw new IllegalArgumentException(
                        "One of the following required parameters for OnApplicationComplete.UpdateApplicationOC is missing: approvalProgram, clearProgram"
                );
            if (globalBytes != null || globalInts != null || localBytes != null || localInts != null || extraPages != null)
                throw new IllegalArgumentException(
                        "One of the following application creation parameters were set on a non-creation call: " +
                                "numGlobalInts, numGlobalByteSlices, numLocalInts, numLocalByteSlices, extraPages"
                );
        } else {
            if (approvalProgram != null || clearProgram != null || globalInts != null || localInts != null || globalBytes != null || localBytes != null || extraPages != null) {
                throw new IllegalArgumentException(
                        "One of the following application creation parameters were set on a non-creation call: " +
                                "approvalProgram, clearProgram, numGlobalInts, numGlobalByteSlices, numLocalInts, numLocalByteSlices, extraPages"
                );
            }
        }
        this.appID = appID;
        this.method = method;
        this.methodArgs = methodArgs;
        this.sender = sender;
        this.suggestedParams = sp;
        this.onCompletion = onCompletion;
        this.note = note;
        this.lease = lease;
        this.fv = fv;
        this.lv = lv;
        this.fee = fee;
        this.flatFee = flatFee;
        this.rekeyTo = rekeyTo;
        this.signer = signer;
        this.foreignAccounts = fAccounts;
        this.foreignAssets = fAssets;
        this.foreignApps = fApps;
        this.approvalProgram = approvalProgram;
        this.clearProgram = clearProgram;
        this.globalBytes = globalBytes;
        this.globalInts = globalInts;
        this.localBytes = localBytes;
        this.localInts = localInts;
        this.extraPages = extraPages;
    }

    public static class Builder {
        public Long appID;
        public Method method;
        public List<Object> methodArgs;
        public String sender, rekeyTo;
        public Transaction.OnCompletion onCompletion;
        public byte[] note, lease;
        public BigInteger fv, lv, fee, flatFee;
        TxnSigner signer;
        public TransactionParams sp;
        public List<Address> foreignAccounts = new ArrayList<>();
        public List<Long> foreignAssets = new ArrayList<>();
        public List<Long> foreignApps = new ArrayList<>();

        public TEALProgram approvalProgram, clearProgram;
        public Long globalInts, globalBytes;
        public Long localInts, localBytes;
        public Long extraPages;

        public Builder() {
            this.onCompletion = Transaction.OnCompletion.NoOpOC;
            this.methodArgs = new ArrayList<>();
        }

        public Builder setAppID(Long appID) {
            this.appID = appID;
            return this;
        }

        public Builder setMethod(Method method) {
            this.method = method;
            return this;
        }

        public Builder addMethodArgs(Object ma) {
            this.methodArgs.add(ma);
            return this;
        }

        public Builder setSender(String sender) {
            this.sender = sender;
            return this;
        }

        public Builder setSuggestedParams(TransactionParams sp) {
            this.sp = sp;
            return this;
        }

        public Builder setOnComplete(Transaction.OnCompletion op) {
            this.onCompletion = op;
            return this;
        }

        public Builder setNote(byte[] note) {
            this.note = note;
            return this;
        }

        public Builder setLease(byte[] lease) {
            this.lease = lease;
            return this;
        }

        public Builder setRekeyTo(String rekeyTo) {
            this.rekeyTo = rekeyTo;
            return this;
        }

        public Builder setSigner(TxnSigner signer) {
            this.signer = signer;
            return this;
        }

        public Builder setFirstValid(BigInteger fv) {
            this.fv = fv;
            return this;
        }

        public Builder setLastValid(BigInteger lv) {
            this.lv = lv;
            return this;
        }

        public Builder setFee(BigInteger fee) {
            this.fee = fee;
            return this;
        }

        public Builder setFlatFee(BigInteger flatFee) {
            this.flatFee = flatFee;
            return this;
        }

        public Builder setForeignAccounts(List<Address> fAccounts) {
            if (fAccounts == null) return this;
            this.foreignAccounts = new ArrayList<>(new HashSet<>(fAccounts));
            return this;
        }

        public Builder setForeignAssets(List<Long> fAssets) {
            if (fAssets == null) return this;
            this.foreignAssets = new ArrayList<>(new HashSet<>(fAssets));
            return this;
        }

        public Builder setForeignApps(List<Long> fApps) {
            if (fApps == null) return this;
            this.foreignApps = new ArrayList<>(new HashSet<>(fApps));
            return this;
        }

        public Builder setApprovalProgram(TEALProgram approvalProgram) {
            if (approvalProgram == null) return this;
            this.approvalProgram = approvalProgram;
            return this;
        }

        public Builder setClearProgram(TEALProgram clearProgram) {
            if (clearProgram == null) return this;
            this.clearProgram = clearProgram;
            return this;
        }

        public Builder setGlobalInts(Long globalInts) {
            this.globalInts = globalInts;
            return this;
        }

        public Builder setGlobalBytes(Long globalBytes) {
            this.globalBytes = globalBytes;
            return this;
        }

        public Builder setLocalInts(Long localInts) {
            this.localInts = localInts;
            return this;
        }

        public Builder setLocalBytes(Long localBytes) {
            this.localBytes = localBytes;
            return this;
        }

        public Builder setExtraPages(Long extraPages) {
            this.extraPages = extraPages;
            return this;
        }

        public MethodCallParams build() {
            return new MethodCallParams(
                    appID, method, methodArgs, sender, sp, onCompletion, note, lease,
                    fv, lv, fee, flatFee, rekeyTo, signer, foreignAccounts, foreignAssets, foreignApps,
                    approvalProgram, clearProgram, globalInts, globalBytes, localInts, localBytes, extraPages
            );
        }
    }
}
