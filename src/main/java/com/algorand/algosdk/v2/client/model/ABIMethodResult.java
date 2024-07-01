package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.abi.Method;
import com.algorand.algosdk.v2.client.algod.PendingTransactionInformation;

import java.util.Arrays;

public class ABIMethodResult {
    // The TxID of the transaction that invoked the ABI method call.
    private String txID;
    // Information about the confirmed transaction that invoked the ABI method call.
    private PendingTransactionResponse pendingTransactionResponse;
    // Method that was called for this ABIMethodResult.
    private Method method;
    // The raw bytes of the return value from the ABI method call. This will be empty if the method does not return a value (return type "void").
    private byte[] rawReturnValue;
    // The return value from the ABI method call. This will be null if the method does not return a value (return type "void"), or if the SDK was unable to decode the returned value.
    private Object returnValue;
    // If the SDK was unable to decode a return value, the error will be here. Make sure to check this before examining returnValue.
    private Exception decodeError;

    // Getters and setters for all fields

    public String getTxID() {
        return txID;
    }

    public void setTxID(String txID) {
        this.txID = txID;
    }

    public PendingTransactionResponse getPendingTransactionResponse() {
        return pendingTransactionResponse;
    }

    public void setTransactionInfo(PendingTransactionResponse pendingTransactionResponse) {
        this.pendingTransactionResponse = pendingTransactionResponse;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public byte[] getRawReturnValue() {
        return rawReturnValue;
    }

    public void setRawReturnValue(byte[] rawReturnValue) {
        this.rawReturnValue = rawReturnValue;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    public Exception getDecodeError() {
        return decodeError;
    }

    public void setDecodeError(Exception decodeError) {
        this.decodeError = decodeError;
    }

    // Optional: Override toString for easier debugging and logging
    @Override
    public String toString() {
        return "ABIMethodResult{" +
                "txID='" + txID + '\'' +
                ", transactionInfo=" + pendingTransactionResponse +
                ", method=" + method +
                ", rawReturnValue=" + Arrays.toString(rawReturnValue) +
                ", returnValue=" + returnValue +
                ", decodeError=" + decodeError +
                '}';
    }
}
