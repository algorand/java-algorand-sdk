package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.Transaction;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

/**
 * Build a payment transaction.
 */
public class PaymentTransactionBuilder<T extends PaymentTransactionBuilder<T>> extends TransactionBuilder<T> {
    protected BigInteger amount = null;
    protected Address receiver = null;
    protected Address closeRemainderTo = null;

    /**
     * Initialize a {@link PaymentTransactionBuilder}.
     */
    public static PaymentTransactionBuilder<?> Builder() {
        return new PaymentTransactionBuilder<>();
    }

    protected PaymentTransactionBuilder() {
        super(Transaction.Type.Payment);
    }

    @Override
    protected Transaction buildInternal() {
        return Transaction.createPaymentTransaction(sender, fee, firstValid, lastValid, note, genesisID, genesisHash, amount,
                receiver, closeRemainderTo);
    }

    /**
     * Set the amount.
     * @param amount
     */
    public T amount(BigInteger amount) {
        this.amount = amount;
        return (T) this;
    }

    /**
     * Set the amount.
     * @param amount
     */
    public T amount(Integer amount) {
        if (amount < 0) throw new IllegalArgumentException("amount cannot be a negative value");
        this.amount = BigInteger.valueOf(amount);
        return (T) this;
    }

    /**
     * Set the amount.
     * @param amount
     */
    public T amount(Long amount) {
        if (amount < 0) throw new IllegalArgumentException("amount cannot be a negative value");
        this.amount = BigInteger.valueOf(amount);
        return (T) this;
    }

    /**
     * Set the receiver.
     * @param receiver
     */
    public T receiver(Address receiver) {
        this.receiver = receiver;
        return (T) this;
    }

    /**
     * Set the receiver.
     * @param receiver
     */
    public T receiver(String receiver) {
        try {
            this.receiver = new Address(receiver);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        return (T) this;
    }

    /**
     * Set the receiver.
     * @param receiver
     */
    public T receiver(byte[] receiver) {
        this.receiver = new Address(receiver);
        return (T) this;
    }

    /**
     * Set the closeRemainderTo.
     * @param closeRemainderTo
     */
    public T closeRemainderTo(Address closeRemainderTo) {
        this.closeRemainderTo = closeRemainderTo;
        return (T) this;
    }

    /**
     * Set the closeRemainderTo.
     * @param closeRemainderTo
     */
    public T closeRemainderTo(String closeRemainderTo) {
        try {
            this.closeRemainderTo = new Address(closeRemainderTo);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        return (T) this;
    }

    /**
     * Set the closeRemainderTo.
     * @param closeRemainderTo
     */
    public T closeRemainderTo(byte[] closeRemainderTo) {
        this.closeRemainderTo = new Address(closeRemainderTo);
        return (T) this;
    }
}
