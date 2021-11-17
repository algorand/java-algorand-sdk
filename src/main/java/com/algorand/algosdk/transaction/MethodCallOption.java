package com.algorand.algosdk.transaction;

import com.algorand.algosdk.abi.Method;
import com.algorand.algosdk.algod.client.model.TransactionParams;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class MethodCallOption {
    public Long appID;
    public Method method;
    public List<AtomicTransactionComposer.MethodArgument> methodArgs;
    public String sender, rekeyTo;
    public Transaction.OnCompletion onCompletion;
    public Byte[] note, lease;
    public BigInteger fv, lv, fee, flatFee;
    AtomicTransactionComposer.TransactionSigner signer;
    TransactionParams suggestedParams;

    public MethodCallOption(Long appID, Method method, List<AtomicTransactionComposer.MethodArgument> methodArgs, String sender,
                             TransactionParams sp, Transaction.OnCompletion onCompletion, Byte[] note, Byte[] lease,
                             BigInteger fv, BigInteger lv, BigInteger fee, BigInteger flatFee,
                             String rekeyTo, AtomicTransactionComposer.TransactionSigner signer) {
        if (appID == null || method == null || sender == null || onCompletion == null || signer == null || sp == null)
            throw new IllegalArgumentException("Method call builder error: some required field not added");
        if (method.args.size() != methodArgs.size())
            throw new IllegalArgumentException("Method call error: incorrect method arg number provided");
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
    }

    public static class MethodCallOptionBuilder {
        public Long appID;
        public Method method;
        public List<AtomicTransactionComposer.MethodArgument> methodArgs;
        public String sender, rekeyTo;
        public Transaction.OnCompletion onCompletion;
        public Byte[] note, lease;
        public BigInteger fv, lv, fee, flatFee;
        AtomicTransactionComposer.TransactionSigner signer;
        TransactionParams sp;

        public MethodCallOptionBuilder() {
            this.onCompletion = Transaction.OnCompletion.NoOpOC;
            this.methodArgs = new ArrayList<>();
        }

        public MethodCallOption.MethodCallOptionBuilder setAppID(Long appID) {
            this.appID = appID;
            return this;
        }

        public MethodCallOption.MethodCallOptionBuilder setMethod(Method method) {
            this.method = method;
            return this;
        }

        public MethodCallOption.MethodCallOptionBuilder addMethodArgs(AtomicTransactionComposer.MethodArgument ma) {
            this.methodArgs.add(ma);
            return this;
        }

        public MethodCallOption.MethodCallOptionBuilder setSender(String sender) {
            this.sender = sender;
            return this;
        }

        public MethodCallOption.MethodCallOptionBuilder setSuggestedParams(TransactionParams sp) {
            this.sp = sp;
            return this;
        }

        public MethodCallOption.MethodCallOptionBuilder setOnComplete(Transaction.OnCompletion op) {
            this.onCompletion = op;
            return this;
        }

        public MethodCallOption.MethodCallOptionBuilder setNote(Byte[] note) {
            this.note = note;
            return this;
        }

        public MethodCallOption.MethodCallOptionBuilder setLease(Byte[] lease) {
            this.lease = lease;
            return this;
        }

        public MethodCallOption.MethodCallOptionBuilder setRekeyTo(String rekeyTo) {
            this.rekeyTo = rekeyTo;
            return this;
        }

        public MethodCallOption.MethodCallOptionBuilder setSigner(AtomicTransactionComposer.TransactionSigner signer) {
            this.signer = signer;
            return this;
        }

        public MethodCallOption.MethodCallOptionBuilder setFirstValid(BigInteger fv) {
            this.fv = fv;
            return this;
        }

        public MethodCallOption.MethodCallOptionBuilder setLastValid(BigInteger lv) {
            this.lv = lv;
            return this;
        }

        public MethodCallOption.MethodCallOptionBuilder setFee(BigInteger fee) {
            this.fee = fee;
            return this;
        }

        public MethodCallOption.MethodCallOptionBuilder setFlatFee(BigInteger flatFee) {
            this.flatFee = flatFee;
            return this;
        }

        public MethodCallOption build() {
            return new MethodCallOption(appID, method, methodArgs, sender, sp, onCompletion, note, lease, fv, lv, fee, flatFee, rekeyTo, signer);
        }
    }
}
