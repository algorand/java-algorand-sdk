package com.algorand.algosdk.transaction;

import com.algorand.algosdk.abi.Method;
import com.algorand.algosdk.algod.client.model.TransactionParams;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class MethodCallParams {
    public Long appID;
    public Method method;
    public List<Object> methodArgs;
    public String sender, rekeyTo;
    public Transaction.OnCompletion onCompletion;
    public byte[] note, lease;
    public BigInteger fv, lv, fee, flatFee;
    AtomicTransactionComposer.TxnSigner signer;
    TransactionParams suggestedParams;

    public MethodCallParams(Long appID, Method method, List<Object> methodArgs, String sender,
                            TransactionParams sp, Transaction.OnCompletion onCompletion, byte[] note, byte[] lease,
                            BigInteger fv, BigInteger lv, BigInteger fee, BigInteger flatFee,
                            String rekeyTo, AtomicTransactionComposer.TxnSigner signer) {
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

    public static class Builder {
        public Long appID;
        public Method method;
        public List<Object> methodArgs;
        public String sender, rekeyTo;
        public Transaction.OnCompletion onCompletion;
        public byte[] note, lease;
        public BigInteger fv, lv, fee, flatFee;
        AtomicTransactionComposer.TxnSigner signer;
        TransactionParams sp;

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

        public Builder setSigner(AtomicTransactionComposer.TxnSigner signer) {
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

        public MethodCallParams build() {
            return new MethodCallParams(appID, method, methodArgs, sender, sp, onCompletion, note, lease, fv, lv, fee, flatFee, rekeyTo, signer);
        }
    }
}
