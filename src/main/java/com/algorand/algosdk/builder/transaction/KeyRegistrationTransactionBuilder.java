package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.crypto.ParticipationPublicKey;
import com.algorand.algosdk.crypto.VRFPublicKey;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;

import java.math.BigInteger;

/**
 * Build a keyreg transaction.
 */
public class KeyRegistrationTransactionBuilder<T extends KeyRegistrationTransactionBuilder<T>> extends TransactionBuilder<T> {
    // votePK is the participation public key used in key registration transactions
    private ParticipationPublicKey votePK = new ParticipationPublicKey();
    // selectionPK is the VRF private key used in key registration transactions
    private VRFPublicKey selectionPK = new VRFPublicKey();
    // voteFirst is the first round this keyreg tx is valid for
    private BigInteger voteFirst = BigInteger.valueOf(0);
    // voteLast is the last round this keyreg tx is valid for
    private BigInteger voteLast = BigInteger.valueOf(0);
    // voteKeyDilution
    private BigInteger voteKeyDilution = BigInteger.valueOf(0);

    /**
     * Initialize a {@link KeyRegistrationTransactionBuilder}.
     */
    public static KeyRegistrationTransactionBuilder<?> Builder() {
        return new KeyRegistrationTransactionBuilder<>();
    }

    public KeyRegistrationTransactionBuilder() {
        super(Transaction.Type.KeyRegistration);
    }

    @Override
    protected Transaction buildInternal() {
        return Transaction.createKeyRegistrationTransaction(sender, fee, firstValid, lastValid, note, genesisID, genesisHash,
                votePK, selectionPK, voteFirst, voteLast, voteKeyDilution);
    }

    /**
     * Set participation public key.
     */
    public T participationPublicKey(ParticipationPublicKey pk) {
        this.votePK = pk;
        return (T) this;
    }

    /**
     * Set participation public key.
     */
    public T participationPublicKey(byte[] pk) {
        this.votePK = new ParticipationPublicKey(pk);
        return (T) this;
    }

    /**
     * Set participation public key.
     */
    public T participationPublicKeyBase64(String pk) {
        this.votePK = new ParticipationPublicKey(Encoder.decodeFromBase64(pk));
        return (T) this;
    }

    /**
     * Set selection public key.
     */
    public T selectionPublicKey(VRFPublicKey pk) {
        this.selectionPK = pk;
        return (T) this;
    }

    /**
     * Set selection public key.
     */
    public T selectionPublicKey(byte[] pk) {
        this.selectionPK = new VRFPublicKey(pk);
        return (T) this;
    }

    /**
     * Set selection public key.
     */
    public T selectionPublicKeyBase64(String pk) {
        this.selectionPK = new VRFPublicKey(Encoder.decodeFromBase64(pk));
        return (T) this;
    }

    /**
     * Set the voteFirst.
     * @param voteFirst
     */
    public T voteFirst(BigInteger voteFirst) {
        this.voteFirst = voteFirst;
        return (T) this;
    }

    /**
     * Set the voteFirst.
     * @param voteFirst
     */
    public T voteFirst(Integer voteFirst) {
        if (voteFirst < 0) throw new IllegalArgumentException("voteFirst cannot be a negative value");
        this.voteFirst = BigInteger.valueOf(voteFirst);
        return (T) this;
    }

    /**
     * Set the voteFirst.
     * @param voteFirst
     */
    public T voteFirst(Long voteFirst) {
        if (voteFirst < 0) throw new IllegalArgumentException("voteFirst cannot be a negative value");
        this.voteFirst = BigInteger.valueOf(voteFirst);
        return (T) this;
    }

    /**
     * Set the voteLast.
     * @param voteLast
     */
    public T voteLast(BigInteger voteLast) {
        this.voteLast = voteLast;
        return (T) this;
    }

    /**
     * Set the voteLast.
     * @param voteLast
     */
    public T voteLast(Integer voteLast) {
        if (voteLast < 0) throw new IllegalArgumentException("voteLast cannot be a negative value");
        this.voteLast = BigInteger.valueOf(voteLast);
        return (T) this;
    }

    /**
     * Set the voteLast.
     * @param voteLast
     */
    public T voteLast(Long voteLast) {
        if (voteLast < 0) throw new IllegalArgumentException("voteLast cannot be a negative value");
        this.voteLast = BigInteger.valueOf(voteLast);
        return (T) this;
    }

    /**
     * Set the voteKeyDilution.
     * @param voteKeyDilution
     */
    public T voteKeyDilution(BigInteger voteKeyDilution) {
        this.voteKeyDilution = voteKeyDilution;
        return (T) this;
    }

    /**
     * Set the voteKeyDilution.
     * @param voteKeyDilution
     */
    public T voteKeyDilution(Integer voteKeyDilution) {
        if (voteKeyDilution < 0) throw new IllegalArgumentException("voteKeyDilution cannot be a negative value");
        this.voteKeyDilution = BigInteger.valueOf(voteKeyDilution);
        return (T) this;
    }

    /**
     * Set the voteKeyDilution.
     * @param voteKeyDilution
     */
    public T voteKeyDilution(Long voteKeyDilution) {
        if (voteKeyDilution < 0) throw new IllegalArgumentException("voteKeyDilution cannot be a negative value");
        this.voteKeyDilution = BigInteger.valueOf(voteKeyDilution);
        return (T) this;
    }
}
