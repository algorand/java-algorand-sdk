package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.crypto.ParticipationPublicKey;
import com.algorand.algosdk.crypto.VRFPublicKey;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;

import java.math.BigInteger;

/**
 * Build a keyreg transaction.
 *
 * Required parameters:
 *     genesisHash
 *
 * Optional parameters:
 *     votePK
 *     selectionPK
 *     voteFirst
 *     voteLast
 *     voteKeyDilution
 *
 * Optional global parameters:
 *     fee/flatFee
 *     note
 *     genesisID
 *     group
 *     lease
 */
public class KeyRegistrationTransactionBuilder<T extends KeyRegistrationTransactionBuilder<T>> extends TransactionBuilder<T> {
    // votePK is the participation public key used in key registration transactions
    protected ParticipationPublicKey votePK = new ParticipationPublicKey();
    // selectionPK is the VRF private key used in key registration transactions
    protected VRFPublicKey selectionPK = new VRFPublicKey();
    // voteFirst is the first round this keyreg tx is valid for
    protected BigInteger voteFirst = BigInteger.valueOf(0);
    // voteLast is the last round this keyreg tx is valid for
    protected BigInteger voteLast = BigInteger.valueOf(0);
    // voteKeyDilution
    protected BigInteger voteKeyDilution = BigInteger.valueOf(0);

    /**
     * Initialize a {@link KeyRegistrationTransactionBuilder}.
     */
    public static KeyRegistrationTransactionBuilder<?> Builder() {
        return new KeyRegistrationTransactionBuilder<>();
    }

    protected KeyRegistrationTransactionBuilder() {
        super(Transaction.Type.KeyRegistration);
    }

    @Override
    protected Transaction buildInternal() {
        return Transaction.createKeyRegistrationTransaction(sender, fee, firstValid, lastValid, note, genesisID, genesisHash,
                votePK, selectionPK, voteFirst, voteLast, voteKeyDilution);
    }

    /**
     * Set participation public key used in key registration transactions.
     * @param pk The participation public key.
     * @return This builder.
     */
    public T participationPublicKey(ParticipationPublicKey pk) {
        this.votePK = pk;
        return (T) this;
    }

    /**
     * Set participation public key used in key registration transactions in the raw 32 byte format.
     * @param pk The participation public key.
     * @return This builder.
     */
    public T participationPublicKey(byte[] pk) {
        this.votePK = new ParticipationPublicKey(pk);
        return (T) this;
    }

    /**
     * Set participation public key used in key registration transactions as a base64 encoded representation of the raw 32 byte format.
     * @param pk The participation public key.
     * @return This builder.
     */
    public T participationPublicKeyBase64(String pk) {
        this.votePK = new ParticipationPublicKey(Encoder.decodeFromBase64(pk));
        return (T) this;
    }

    /**
     * Set selection public key for the VRF private key used in key registration transactions.
     * @param pk The public selection key.
     * @return This builder.
     */
    public T selectionPublicKey(VRFPublicKey pk) {
        this.selectionPK = pk;
        return (T) this;
    }

    /**
     * Set selection public key for the VRF private key used in key registration transactions in the raw 32 byte format
     * @param pk The public selection key.
     * @return This builder.
     */
    public T selectionPublicKey(byte[] pk) {
        this.selectionPK = new VRFPublicKey(pk);
        return (T) this;
    }

    /**
     * Set selection public key for the VRF private key used in key registration transactions as a base64 encoded representation of the raw 32 byte format.
     * @param pk The public selection key.
     * @return This builder.
     */
    public T selectionPublicKeyBase64(String pk) {
        this.selectionPK = new VRFPublicKey(Encoder.decodeFromBase64(pk));
        return (T) this;
    }

    /**
     * Set the voteFirst value. It is the first round in which the data in this transaction will be considered.
     * @param voteFirst The voteFirst value.
     * @return This builder.
     */
    public T voteFirst(BigInteger voteFirst) {
        this.voteFirst = voteFirst;
        return (T) this;
    }

    /**
     * Set the voteFirst value. It is the first round in which the data in this transaction will be considered.
     * @param voteFirst The voteFirst value.
     * @return This builder.
     */
    public T voteFirst(Integer voteFirst) {
        if (voteFirst < 0) throw new IllegalArgumentException("voteFirst cannot be a negative value");
        this.voteFirst = BigInteger.valueOf(voteFirst);
        return (T) this;
    }

    /**
     * Set the voteFirst value. It is the first round in which the data in this transaction will be considered.
     * @param voteFirst The voteFirst value.
     * @return This builder.
     */
    public T voteFirst(Long voteFirst) {
        if (voteFirst < 0) throw new IllegalArgumentException("voteFirst cannot be a negative value");
        this.voteFirst = BigInteger.valueOf(voteFirst);
        return (T) this;
    }

    /**
     * Set the voteLast value. It is the last round in which the data in this transaction will be considered.
     * @param voteLast The voteLast value.
     * @return This builder.
     */
    public T voteLast(BigInteger voteLast) {
        this.voteLast = voteLast;
        return (T) this;
    }

    /**
     * Set the voteLast value. It is the last round in which the data in this transaction will be considered.
     * @param voteLast The voteLast value.
     * @return This builder.
     */
    public T voteLast(Integer voteLast) {
        if (voteLast < 0) throw new IllegalArgumentException("voteLast cannot be a negative value");
        this.voteLast = BigInteger.valueOf(voteLast);
        return (T) this;
    }

    /**
     * Set the voteLast value. It is the last round in which the data in this transaction will be considered.
     * @param voteLast The voteLast value.
     * @return This builder.
     */
    public T voteLast(Long voteLast) {
        if (voteLast < 0) throw new IllegalArgumentException("voteLast cannot be a negative value");
        this.voteLast = BigInteger.valueOf(voteLast);
        return (T) this;
    }

    /**
     * Set the voteKeyDilution value. This is used to indicate the number of subkeys in each batch of participation keys.
     * @param voteKeyDilution The voteKeyDilution value.
     * @return This builder.
     */
    public T voteKeyDilution(BigInteger voteKeyDilution) {
        this.voteKeyDilution = voteKeyDilution;
        return (T) this;
    }

    /**
     * Set the voteKeyDilution value. This is used to indicate the number of subkeys in each batch of participation keys.
     * @param voteKeyDilution The voteKeyDilution value.
     * @return This builder.
     */
    public T voteKeyDilution(Integer voteKeyDilution) {
        if (voteKeyDilution < 0) throw new IllegalArgumentException("voteKeyDilution cannot be a negative value");
        this.voteKeyDilution = BigInteger.valueOf(voteKeyDilution);
        return (T) this;
    }

    /**
     * Set the voteKeyDilution value. This is used to indicate the number of subkeys in each batch of participation keys.
     * @param voteKeyDilution The voteKeyDilution value.
     * @return This builder.
     */
    public T voteKeyDilution(Long voteKeyDilution) {
        if (voteKeyDilution < 0) throw new IllegalArgumentException("voteKeyDilution cannot be a negative value");
        this.voteKeyDilution = BigInteger.valueOf(voteKeyDilution);
        return (T) this;
    }
}
