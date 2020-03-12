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
    final Transaction.Type type;

    Address sender = null;
    public BigInteger fee = null;
    public BigInteger flatFee = null;
    public BigInteger firstValid = null;
    public BigInteger lastValid = null;
    public byte[] note = null;
    public byte[] lease = null;
    public String genesisID = null;
    public Digest genesisHash = null;
    public Digest group = null;

    protected TransactionBuilder(Transaction.Type type) {
        this.type = type;
    }

    protected abstract Transaction buildInternal();

    /**
     * Build the Transaction object.
     * @return
     */
    final public Transaction build() {
        if(fee != null && flatFee != null) {
            throw new IllegalArgumentException("Cannot set both fee and flatFee.");
        }

        Transaction txn = buildInternal();

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
     * @param client
     * @return
     * @throws ApiException
     */
    public T lookupParams(AlgodApi client) throws ApiException {
        TransactionParams params = client.transactionParams();
        return suggestedParams(params);
    }

    /**
     * Initialize fee, genesisID, genesisHash, firstValid and lastValid using TransactionParameters.
     * @param params
     * @return
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
     * Set the sender.
     * @param sender
     */
    public T sender(Address sender) {
        this.sender = sender;
        return (T) this;
    }

    /**
     * Set the sender.
     * @param sender
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
     * Set the sender.
     * @param sender
     */
    public T sender(byte[] sender) {
        this.sender = new Address(sender);
        return (T) this;
    }

    /**
     * Set the fee.
     * @param fee
     */
    public T fee(BigInteger fee) {
        this.fee = fee;
        return (T) this;
    }

    /**
     * Set the fee.
     * @param fee
     */
    public T fee(Integer fee) {
        if (fee < 0) throw new IllegalArgumentException("fee cannot be a negative value");
        this.fee = BigInteger.valueOf(fee);
        return (T) this;
    }

    /**
     * Set the fee.
     * @param fee
     */
    public T fee(Long fee) {
        if (fee < 0) throw new IllegalArgumentException("fee cannot be a negative value");
        this.fee = BigInteger.valueOf(fee);
        return (T) this;
    }

    /**
     * Set the flatFee.
     * @param flatFee
     */
    public T flatFee(BigInteger flatFee) {
        this.flatFee = flatFee;
        return (T) this;
    }

    /**
     * Set the flatFee.
     * @param flatFee
     */
    public T flatFee(Integer flatFee) {
        if (flatFee < 0) throw new IllegalArgumentException("flatFee cannot be a negative value");
        this.flatFee = BigInteger.valueOf(flatFee);
        return (T) this;
    }

    /**
     * Set the flatFee.
     * @param flatFee
     */
    public T flatFee(Long flatFee) {
        if (flatFee < 0) throw new IllegalArgumentException("flatFee cannot be a negative value");
        this.flatFee = BigInteger.valueOf(flatFee);
        return (T) this;
    }

    /**
     * Set the firstValid.
     * @param firstValid
     */
    public T firstValid(BigInteger firstValid) {
        this.firstValid = firstValid;
        return (T) this;
    }

    /**
     * Set the firstValid.
     * @param firstValid
     */
    public T firstValid(Integer firstValid) {
        if (firstValid < 0) throw new IllegalArgumentException("firstValid cannot be a negative value");
        this.firstValid = BigInteger.valueOf(firstValid);
        return (T) this;
    }

    /**
     * Set the firstValid.
     * @param firstValid
     */
    public T firstValid(Long firstValid) {
        if (firstValid < 0) throw new IllegalArgumentException("firstValid cannot be a negative value");
        this.firstValid = BigInteger.valueOf(firstValid);
        return (T) this;
    }

    /**
     * Set the lastValid.
     * @param lastValid
     */
    public T lastValid(BigInteger lastValid) {
        this.lastValid = lastValid;
        return (T) this;
    }

    /**
     * Set the lastValid.
     * @param lastValid
     */
    public T lastValid(Integer lastValid) {
        if (lastValid < 0) throw new IllegalArgumentException("lastValid cannot be a negative value");
        this.lastValid = BigInteger.valueOf(lastValid);
        return (T) this;
    }

    /**
     * Set the lastValid.
     * @param lastValid
     */
    public T lastValid(Long lastValid) {
        if (lastValid < 0) throw new IllegalArgumentException("lastValid cannot be a negative value");
        this.lastValid = BigInteger.valueOf(lastValid);
        return (T) this;
    }

    /**
     * Set the note field.
     * @param note
     */
    public T note(byte[] note) {
        this.note = note;
        return (T) this;
    }
    /**
     * Set the note field using a UTF-8 encoded string.
     * @param note
     */
    public T noteUTF8(String note) {
        this.note = note.getBytes(StandardCharsets.UTF_8);
        return (T) this;
    }

    /**
     * Set the note field using a Base64 encoded string.
     * @param note
     */
    public T noteB64(String note) {
        this.note = Encoder.decodeFromBase64(note);
        return (T) this;
    }

    /**
     * Set the lease field.
     * @param lease
     */
    public T lease(Lease lease) {
        this.lease = lease.getBytes();
        return (T) this;
    }

    /**
     * Set the lease field.
     * @param lease
     */
    public T lease(byte[] lease) {
        this.lease = lease;
        return (T) this;
    }

    /**
     * Set the lease field using a UTF-8 encoded string.
     * @param lease
     */
    public T leaseUTF8(String lease) {
        this.lease = lease.getBytes(StandardCharsets.UTF_8);
        return (T) this;
    }

    /**
     * Set the lease field using a Base64 encoded string.
     * @param lease
     */
    public T leaseB64(String lease) {
        this.lease = Encoder.decodeFromBase64(lease);
        return (T) this;
    }

    /**
     * Set the genesisID.
     * @param genesisID
     */
    public T genesisID(String genesisID) {
        this.genesisID = genesisID;
        return (T) this;
    }

    /**
     * Set the genesisHash field.
     * @param genesisHash
     */
    public T genesisHash(Digest genesisHash) {
        this.genesisHash = genesisHash;
        return (T) this;
    }

    /**
     * Set the genesisHash field.
     * @param genesisHash
     */
    public T genesisHash(byte[] genesisHash) {
        this.genesisHash = new Digest(genesisHash);
        return (T) this;
    }
    /**
     * Set the genesisHash field using a UTF-8 encoded string.
     * @param genesisHash
     */
    public T genesisHashUTF8(String genesisHash) {
        this.genesisHash = new Digest(genesisHash.getBytes(StandardCharsets.UTF_8));
        return (T) this;
    }

    /**
     * Set the genesisHash field using a Base64 encoded string.
     * @param genesisHash
     */
    public T genesisHashB64(String genesisHash) {
        this.genesisHash = new Digest(Encoder.decodeFromBase64(genesisHash));
        return (T) this;
    }

    /**
     * Set the group field.
     * @param group
     */
    public T group(Digest group) {
        this.group = group;
        return (T) this;
    }

    /**
     * Set the group field.
     * @param group
     */
    public T group(byte[] group) {
        this.group = new Digest(group);
        return (T) this;
    }
    /**
     * Set the group field using a UTF-8 encoded string.
     * @param group
     */
    public T groupUTF8(String group) {
        this.group = new Digest(group.getBytes(StandardCharsets.UTF_8));
        return (T) this;
    }

    /**
     * Set the group field using a Base64 encoded string.
     * @param group
     */
    public T groupB64(String group) {
        this.group = new Digest(Encoder.decodeFromBase64(group));
        return (T) this;
    }
}

