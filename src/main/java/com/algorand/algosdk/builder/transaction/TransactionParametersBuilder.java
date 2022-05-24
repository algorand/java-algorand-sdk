package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.algod.client.ApiException;
import com.algorand.algosdk.algod.client.api.AlgodApi;
import com.algorand.algosdk.algod.client.model.TransactionParams;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.transaction.Lease;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

@SuppressWarnings("unchecked")
public abstract class TransactionParametersBuilder<T extends TransactionParametersBuilder<T>> {

    protected Address sender = null;
    protected BigInteger fee = null;
    protected BigInteger flatFee = null;
    protected BigInteger firstValid = null;
    protected BigInteger lastValid = null;
    protected byte[] note = null;
    protected byte[] lease = null;
    protected Address rekeyTo = null;
    protected String genesisID = null;
    protected Digest genesisHash = null;

    /**
     * Query the V1 REST API with {@link AlgodApi} for Transaction Parameters:
     * Initialize fee, genesisID, genesisHash, firstValid, lastValid by querying algod if not already set.
     * @param client The backend client connection.
     * @return This builder.
     * @throws ApiException When the client fails to retrieve {@link TransactionParams} from the backend.
     */
    public T lookupParams(AlgodApi client) throws ApiException {
        TransactionParams params = client.transactionParams();
        return suggestedParams(params);
    }

    /**
     * Initialize fee, genesisID, genesisHash, firstValid, lastValid using {@link TransactionParams} if not already set.
     * @param params The suggested transaction parameters.
     * @return This builder.
     */
    public T suggestedParams(TransactionParams params) {
        if (this.fee == null) {
            fee(params.getFee());
        }
        if (this.genesisID == null) {
            genesisID(params.getGenesisID());
        }
        if (this.genesisHash == null) {
            genesisHash(params.getGenesishashb64());
        }
        if (this.firstValid == null) {
            firstValid(params.getLastRound());
        }
        if (this.lastValid == null) {
            lastValid(params.getLastRound().add(BigInteger.valueOf(1000L)));
        }
        return (T) this;
    }

    /**
     * Query the V2 REST API with {@link com.algorand.algosdk.v2.client.common.AlgodClient} for Transaction Parameters:
     * Initialize fee, genesisID, genesisHash, firstValid, lastValid using {@link TransactionParametersResponse} if not already set.
     * @param client The backend client connection.
     * @return This builder.
     * @throws ApiException When the client fails to retrieve {@link TransactionParametersResponse} from the backend.
     */
    public T lookupParams(com.algorand.algosdk.v2.client.common.AlgodClient client) throws Exception {
        Response<TransactionParametersResponse> params = client.TransactionParams().execute();
        return suggestedParams(params.body());
    }

    /**
     * Initialize fee, genesisID, genesisHash, firstValid and lastValid using {@link TransactionParametersResponse}.
     * @param params The suggested transaction parameters.
     * @return This builder.
     */
    public T suggestedParams(TransactionParametersResponse params) {
        fee(params.fee);
        genesisID(params.genesisId);
        genesisHash(params.genesisHash);
        firstValid(params.lastRound);
        lastValid(params.lastRound + 1000);
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
     * Set the flatFee. This value will be used for the transaction fee.
     * This fee may fall to zero but a group of N atomic transactions must
     * still have a fee of at least N*MinTxnFee.
     * This field cannot be combined with fee.
     * @param flatFee The flatFee to use for the transaction.
     * @return This builder.
     */
    public T flatFee(BigInteger flatFee) {
        this.flatFee = flatFee;
        return (T) this;
    }

    /**
     * Set the flatFee. This value will be used for the transaction fee.
     * This fee may fall to zero but a group of N atomic transactions must
     * still have a fee of at least N*MinTxnFee.
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
     * Set the flatFee. This value will be used for the transaction fee.
     * This fee may fall to zero but a group of N atomic transactions must
     * still have a fee of at least N*MinTxnFee.
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
     * Rekey to the sender account.
     * @return This builder.
     */
    public T rekey(Address rekeyTo) {
        this.rekeyTo = rekeyTo;
        return (T) this;
    }

    /**
     * Rekey to the account in the human-readable address format.
     * @return This builder.
     */
    public T rekey(String rekeyTo) {
        try {
            this.rekeyTo = new Address(rekeyTo);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        return (T) this;
    }

    /**
     * Rekey to the account in the raw 32 byte format.
     * @return This builder.
     */
    public T rekey(byte[] rekeyTo) {
        this.rekeyTo = new Address(rekeyTo);
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
}
