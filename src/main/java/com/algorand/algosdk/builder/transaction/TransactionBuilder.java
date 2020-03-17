package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.algod.client.ApiException;
import com.algorand.algosdk.algod.client.api.AlgodApi;
import com.algorand.algosdk.algod.client.model.TransactionParams;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.transaction.Lease;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

/**
 * TransactionBuilder has parameters common to all transactions types.
 */
public abstract class TransactionBuilder<T extends TransactionBuilder<T>> {
    protected final Transaction.Type type;

    protected Address sender = null;
    protected BigInteger fee = null;
    protected BigInteger flatFee = null;
    protected BigInteger firstValid = null;
    protected BigInteger lastValid = null;
    protected byte[] note = null;
    protected byte[] lease = null;
    protected String genesisID = null;
    protected Digest genesisHash = null;
    protected Digest group = null;

    protected TransactionBuilder(Transaction.Type type) {
        this.type = type;
    }

    protected abstract Transaction buildInternal();

    /**
     * Build the Transaction object. An exception is thrown if a valid transaction cannot be created with the provided
     * fields.
     * @return A transaction.
     */
    final public Transaction build() {
        if(fee != null && flatFee != null) {
            throw new IllegalArgumentException("Cannot set both fee and flatFee.");
        }

        if (lastValid == null && firstValid != null) {
            lastValid = firstValid.add(BigInteger.valueOf(1000));
        }

        Transaction txn = buildInternal();

        if(lease != null) {
            txn.setLease(lease);
        }

        if(fee != null) {
            try {
                Account.setFeeByFeePerByte(txn, fee);
            } catch (NoSuchAlgorithmException e) {
                throw new UnsupportedOperationException(e);
            }
        }
        if (flatFee != null) {
            txn.setFee(flatFee);
        }

        return txn;
    }

    /**
     * Query the Algorand REST endpoint for Transaction Parameters:
     * Initialize fee, genesisID, genesisHash, firstValid, lastValid using TransactionParameters.
     * @param client The backend client connection.
     * @return This builder.
     * @throws ApiException When the client fails to retrieve {@link TransactionParams} from the backend.
     */
    public T lookupParams(AlgodApi client) throws ApiException {
        TransactionParams params = client.transactionParams();
        return suggestedParams(params);
    }

    /**
     * Initialize fee, genesisID, genesisHash, firstValid and lastValid using {@link TransactionParams}.
     * @param params The suggested transaction parameters.
     * @return This builder.
     */
    public T suggestedParams(TransactionParams params) {
        fee(params.getFee());
        genesisID(params.getGenesisID());
        genesisHash(params.getGenesishashb64());
        firstValid(params.getLastRound());
        lastValid(params.getLastRound().add(BigInteger.valueOf(1000L)));
        return (T) this;
    }

    /**
     * Set the transaction sender account.
     * @param sender The sender account.
     * @return This builder.
     */
    public T sender(Address sender) {
        this.sender = sender;
        return (T) this;
    }

    /**
     * Set the transaction sender account in the human-readable address format.
     * @param sender The sender account.
     * @return This builder.
     */
    public T sender(String sender) {
        try {
            this.sender = new Address(sender);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        return (T) this;
    }

    /**
     * Set the transaction sender account in the raw 32 byte format.
     * @param sender The sender account.
     * @return This builder.
     */
    public T sender(byte[] sender) {
        this.sender = new Address(sender);
        return (T) this;
    }

    /**
     * Set the fee per bytes value. This value is multiplied by the estimated size of the transaction to reach a final transaction fee, or 1000, whichever is higher.
     * This field cannot be combined with flatFee.
     * @param fee The fee per byte.
     * @return This builder.
     */
    public T fee(BigInteger fee) {
        this.fee = fee;
        return (T) this;
    }

    /**
     * Set the fee per bytes value. This value is multiplied by the estimated size of the transaction to reach a final transaction fee, or 1000, whichever is higher.
     * This field cannot be combined with flatFee.
     * @param fee The fee per byte.
     * @return This builder.
     */
    public T fee(Integer fee) {
        if (fee < 0) throw new IllegalArgumentException("fee cannot be a negative value");
        this.fee = BigInteger.valueOf(fee);
        return (T) this;
    }

    /**
     * Set the fee per bytes value. This value is multiplied by the estimated size of the transaction to reach a final transaction fee, or 1000, whichever is higher.
     * This field cannot be combined with flatFee.
     * @param fee The fee per byte.
     * @return This builder.
     */
    public T fee(Long fee) {
        if (fee < 0) throw new IllegalArgumentException("fee cannot be a negative value");
        this.fee = BigInteger.valueOf(fee);
        return (T) this;
    }

    /**
     * Set the flatFee. This value will be used for the transaction fee, or 1000, whichever is higher.
     * This field cannot be combined with fee.
     * @param flatFee The flatFee to use for the transaction.
     * @return This builder.
     */
    public T flatFee(BigInteger flatFee) {
        this.flatFee = flatFee;
        return (T) this;
    }

    /**
     * Set the flatFee. This value will be used for the transaction fee, or 1000, whichever is higher.
     * This field cannot be combined with fee.
     * @param flatFee The flatFee to use for the transaction.
     * @return This builder.
     */
    public T flatFee(Integer flatFee) {
        if (flatFee < 0) throw new IllegalArgumentException("flatFee cannot be a negative value");
        this.flatFee = BigInteger.valueOf(flatFee);
        return (T) this;
    }

    /**
     * Set the flatFee. This value will be used for the transaction fee, or 1000, whichever is higher.
     * This field cannot be combined with fee.
     * @param flatFee The flatFee to use for the transaction.
     * @return This builder.
     */
    public T flatFee(Long flatFee) {
        if (flatFee < 0) throw new IllegalArgumentException("flatFee cannot be a negative value");
        this.flatFee = BigInteger.valueOf(flatFee);
        return (T) this;
    }

    /**
     * Set the firstValid value, which is the first round that this transaction is valid for.
     * @param firstValid The firstValid round.
     * @return This builder.
     */
    public T firstValid(BigInteger firstValid) {
        this.firstValid = firstValid;
        return (T) this;
    }

    /**
     * Set the firstValid value, which is the first round that this transaction is valid for.
     * @param firstValid The firstValid round.
     * @return This builder.
     */
    public T firstValid(Integer firstValid) {
        if (firstValid < 0) throw new IllegalArgumentException("firstValid cannot be a negative value");
        this.firstValid = BigInteger.valueOf(firstValid);
        return (T) this;
    }

    /**
     * Set the firstValid value, which is the first round that this transaction is valid for.
     * @param firstValid The firstValid round.
     * @return This builder.
     */
    public T firstValid(Long firstValid) {
        if (firstValid < 0) throw new IllegalArgumentException("firstValid cannot be a negative value");
        this.firstValid = BigInteger.valueOf(firstValid);
        return (T) this;
    }

    /**
     * Set the lastValid value, which is the last round that this transaction is valid for.
     * @param lastValid The lastValid round.
     * @return This builder.
     */
    public T lastValid(BigInteger lastValid) {
        this.lastValid = lastValid;
        return (T) this;
    }

    /**
     * Set the lastValid value, which is the last round that this transaction is valid for.
     * @param lastValid The lastValid round.
     * @return This builder.
     */
    public T lastValid(Integer lastValid) {
        if (lastValid < 0) throw new IllegalArgumentException("lastValid cannot be a negative value");
        this.lastValid = BigInteger.valueOf(lastValid);
        return (T) this;
    }

    /**
     * Set the lastValid value, which is the last round that this transaction is valid for.
     * @param lastValid The lastValid round.
     * @return This builder.
     */
    public T lastValid(Long lastValid) {
        if (lastValid < 0) throw new IllegalArgumentException("lastValid cannot be a negative value");
        this.lastValid = BigInteger.valueOf(lastValid);
        return (T) this;
    }

    /**
     * Set the note field. It may containe 1024 bytes of free form data.
     * @param note The note field.
     * @return This builder.
     */
    public T note(byte[] note) {
        this.note = note;
        return (T) this;
    }
    /**
     * Set the note field using a UTF-8 encoded string. It may containe 1024 bytes of free form data.
     * @param note The note field.
     * @return This builder.
     */
    public T noteUTF8(String note) {
        this.note = note.getBytes(StandardCharsets.UTF_8);
        return (T) this;
    }

    /**
     * Set the note field using a Base64 encoded string. It may containe 1024 bytes of free form data.
     * @param note The note field.
     * @return This builder.
     */
    public T noteB64(String note) {
        this.note = Encoder.decodeFromBase64(note);
        return (T) this;
    }

    /**
     * Set the optional lease field.  Lease enforces mutual exclusion of transactions.  If this field is nonzero, then once the
     * transaction is confirmed, it acquires the lease identified by the (Sender, Lease) pair of the transaction until
     * the LastValid round passes.  While this transaction possesses the lease, no other transaction specifying this
     * lease can be confirmed.
     * @param lease The lease.
     * @return This builder.
     */
    public T lease(Lease lease) {
        this.lease = lease.getBytes();
        return (T) this;
    }

    /**
     * Set the optional lease field in its raw 32 byte form.  Lease enforces mutual exclusion of transactions.  If this field is nonzero, then once the
     * transaction is confirmed, it acquires the lease identified by the (Sender, Lease) pair of the transaction until
     * the LastValid round passes.  While this transaction possesses the lease, no other transaction specifying this
     * lease can be confirmed.
     * @param lease The lease.
     * @return This builder.
     * @param lease
     */
    public T lease(byte[] lease) {
        this.lease = lease;
        return (T) this;
    }

    /**
     * Set the optional lease field using a UTF-8 encoded string.  Lease enforces mutual exclusion of transactions.  If this field is nonzero, then once the
     * transaction is confirmed, it acquires the lease identified by the (Sender, Lease) pair of the transaction until
     * the LastValid round passes.  While this transaction possesses the lease, no other transaction specifying this
     * lease can be confirmed.
     * @param lease The lease.
     * @return This builder.
     */
    public T leaseUTF8(String lease) {
        this.lease = lease.getBytes(StandardCharsets.UTF_8);
        return (T) this;
    }

    /**
     * Set the optional lease field using a base 64 encoded string.  Lease enforces mutual exclusion of transactions.  If this field is nonzero, then once the
     * transaction is confirmed, it acquires the lease identified by the (Sender, Lease) pair of the transaction until
     * the LastValid round passes.  While this transaction possesses the lease, no other transaction specifying this
     * lease can be confirmed.
     * @param lease The lease.
     * @return This builder.
     */
    public T leaseB64(String lease) {
        this.lease = Encoder.decodeFromBase64(lease);
        return (T) this;
    }

    /**
     * Set the optional genesisID. If set it is used to verify that the transaction is being submitted to the correct blockchain.
     * @param genesisID The genesisID.
     * @return This builder.
     */
    public T genesisID(String genesisID) {
        this.genesisID = genesisID;
        return (T) this;
    }

    /**
     * Set the genesisHash field. It must match the hash of the genesis block and is used to verify that that the
     * transaction is being submitted to the correct blockchain.
     * @param genesisHash The genesisHash.
     * @return This builder.
     */
    public T genesisHash(Digest genesisHash) {
        this.genesisHash = genesisHash;
        return (T) this;
    }

    /**
     * Set the genesisHash field in its raw byte array format. It must match the hash of the genesis block and is used
     * to verify that that the transaction is being submitted to the correct blockchain.
     * @param genesisHash The genesisHash.
     * @return This builder.
     */
    public T genesisHash(byte[] genesisHash) {
        this.genesisHash = new Digest(genesisHash);
        return (T) this;
    }
    /**
     * Set the genesisHash field in a UTF8 encoded format. It must match the hash of the genesis block and is used to
     * verify that that the transaction is being submitted to the correct blockchain.
     * @param genesisHash The genesisHash.
     * @return This builder.
     */
    public T genesisHashUTF8(String genesisHash) {
        this.genesisHash = new Digest(genesisHash.getBytes(StandardCharsets.UTF_8));
        return (T) this;
    }

    /**
     * Set the genesisHash field in a base64 encoded format. It must match the hash of the genesis block and is used to
     * verify that that the transaction is being submitted to the correct blockchain.
     * @param genesisHash The genesisHash.
     * @return This builder.
     */
    public T genesisHashB64(String genesisHash) {
        this.genesisHash = new Digest(Encoder.decodeFromBase64(genesisHash));
        return (T) this;
    }

    /**
     * Set the group field. When present indicates that this transaction is part of a transaction group and the value is
     * the sha512/256 hash of the transactions in that group.
     * @param group The group.
     * @return This builder.
     */
    public T group(Digest group) {
        this.group = group;
        return (T) this;
    }

    /**
     * Set the group field as a raw byte array representation. When present indicates that this transaction is part of a
     * transaction group and the value is the sha512/256 hash of the transactions in that group.
     * @param group The group.
     * @return This builder.
     */
    public T group(byte[] group) {
        this.group = new Digest(group);
        return (T) this;
    }
    /**
     * Set the group field as a UTF-8 encoded string. When present indicates that this transaction is part of a
     * transaction group and the value is the sha512/256 hash of the transactions in that group.
     * @param group The group.
     * @return This builder.
     */
    public T groupUTF8(String group) {
        this.group = new Digest(group.getBytes(StandardCharsets.UTF_8));
        return (T) this;
    }

    /**
     * Set the group field as a base64 encoded string. When present indicates that this transaction is part of a
     * transaction group and the value is the sha512/256 hash of the transactions in that group.
     * @param group The group.
     * @return This builder.
     */
    public T groupB64(String group) {
        this.group = new Digest(Encoder.decodeFromBase64(group));
        return (T) this;
    }
}

