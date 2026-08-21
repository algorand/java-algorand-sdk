package com.algorand.algosdk.signer;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.crypto.PQAddress;
import com.algorand.algosdk.crypto.PQSignature;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.transaction.TxnSigner;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

/**
 * A {@link TxnSigner} backed by a post-quantum signing callback (a public key
 * plus a {@link RawSigner} that signs exact bytes) instead of a raw secret
 * key, parameterized by a 2-byte scheme identifier. Besides signing transactions
 * it can delegate a logic signature via {@link #signLogicsig}.
 * <p>
 * The callback receives the full domain-separated signing preimage, matching
 * what consensus verifies ("TX" + txn for transactions, "PQProgram" + address
 * + program for delegated logic signatures). The signer always uses the
 * canonical-salt address derived from the public key, unless an explicit
 * salt is supplied.
 * <p>
 * {@link Falcon1024AlgorandSigner} fixes the scheme to Falcon-1024 and is the
 * signer to reach for; this generic base is public so a scheme the protocol
 * registers later can be driven without waiting for its own subclass, as
 * py-algorand-sdk's PQAlgorandSigner also allows.
 */
public class PQAlgorandSigner implements TxnSigner {
    private final byte[] publicKey;
    private final RawSigner signer;
    private final byte[] scheme;
    private final Address address;
    private final int salt;

    /**
     * @param publicKey the scheme's public key
     * @param signer callback that returns a raw post-quantum signature over the
     *               given preimage bytes
     * @param scheme 2-byte scheme identifier (e.g. {@link PQSignature#falcon1024Scheme()})
     * @throws IllegalArgumentException if the scheme is not exactly 2 bytes
     * @throws NoSuchAlgorithmException if SHA-512/256 is unavailable
     */
    public PQAlgorandSigner(byte[] publicKey, RawSigner signer, byte[] scheme) throws NoSuchAlgorithmException {
        this(publicKey, signer, scheme, null);
    }

    /**
     * Create a signer for an explicit, possibly non-canonical salt.
     * <p>
     * A post-quantum public key has an address for every salt whose digest lands
     * off the ed25519 curve, and consensus resolves the authorizer from the salt
     * carried in the signature, so all of them are usable accounts. This
     * constructor does not check that the resulting address is off-curve.
     * @param publicKey the scheme's public key
     * @param signer callback that returns a raw post-quantum signature over the
     *               given preimage bytes
     * @param scheme 2-byte scheme identifier (e.g. {@link PQSignature#falcon1024Scheme()})
     * @param salt the salt to sign for (0-255)
     * @throws IllegalArgumentException if the scheme is not exactly 2 bytes or the salt is out of range
     * @throws NoSuchAlgorithmException if SHA-512/256 is unavailable
     */
    public PQAlgorandSigner(byte[] publicKey, RawSigner signer, byte[] scheme, int salt) throws NoSuchAlgorithmException {
        this(publicKey, signer, scheme, Integer.valueOf(salt));
    }

    private PQAlgorandSigner(byte[] publicKey, RawSigner signer, byte[] scheme, Integer salt) throws NoSuchAlgorithmException {
        Objects.requireNonNull(publicKey, "publicKey must not be null");
        // Copied so a later mutation of the caller's array cannot desynchronise
        // the serialized key from the address already derived from it.
        this.publicKey = Arrays.copyOf(publicKey, publicKey.length);
        this.signer = Objects.requireNonNull(signer, "signer must not be null");
        this.scheme = scheme == null ? null : Arrays.copyOf(scheme, scheme.length);
        if (salt == null) {
            PQAddress derived = PQAddress.derive(this.scheme, this.publicKey);
            this.address = derived.getAddress();
            this.salt = derived.getSalt();
        } else {
            this.address = PQAddress.derive(this.scheme, salt, this.publicKey);
            this.salt = salt;
        }
    }

    /**
     * @return the post-quantum account address (derived with the canonical salt)
     */
    public Address getAddress() {
        return this.address;
    }

    /**
     * @return the salt this signer's address was derived with (0-255); the
     *         canonical salt unless an explicit one was given
     */
    public int getSalt() {
        return this.salt;
    }

    /**
     * @return the 2-byte scheme identifier
     */
    public byte[] getScheme() {
        return Arrays.copyOf(this.scheme, this.scheme.length);
    }

    /**
     * @return the post-quantum public key
     */
    public byte[] getPublicKey() {
        return Arrays.copyOf(this.publicKey, this.publicKey.length);
    }

    /**
     * Two signers are equal when they authorize the same account: same scheme,
     * same public key and same salt. Salt is part of the identity because
     * signing for a different salt is signing for a different account.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PQAlgorandSigner)) return false;
        PQAlgorandSigner actual = (PQAlgorandSigner) obj;
        return Arrays.equals(this.scheme, actual.scheme)
                && this.salt == actual.salt
                && Arrays.equals(this.publicKey, actual.publicKey);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(this.scheme);
        result = 31 * result + this.salt;
        return 31 * result + Arrays.hashCode(this.publicKey);
    }

    private PQSignature sign(byte[] preimage) throws Exception {
        return new PQSignature(this.scheme, this.salt, this.publicKey, this.signer.sign(preimage));
    }

    @Override
    public SignedTransaction[] signTxnGroup(Transaction[] txnGroup, int[] indicesToSign) throws Exception {
        SignedTransaction[] sTxn = new SignedTransaction[indicesToSign.length];
        for (int i = 0; i < indicesToSign.length; i++) {
            Transaction tx = txnGroup[indicesToSign[i]];
            SignedTransaction stx = new SignedTransaction(tx, this.sign(tx.bytesToSign()), tx.txID());
            // The post-quantum address is the authorizer; attach it as the auth
            // address whenever the transaction is sent by a different (rekeyed)
            // account.
            if (!tx.sender.equals(this.getAddress())) {
                stx.authAddr(this.getAddress());
            }
            sTxn[i] = stx;
        }
        return sTxn;
    }

    /**
     * Sign (delegate) a LogicSig with this signer, in place, with a post-quantum
     * signature over "PQProgram" + address + program.
     * <p>
     * Post-quantum signatures do not support multisig delegation; there is no
     * multisig variant of this method.
     * @param lsig LogicsigSignature to sign
     * @return LogicsigSignature with updated post-quantum signature
     * @throws IllegalStateException if the LogicSig already carries a signature
     */
    public LogicsigSignature signLogicsig(LogicsigSignature lsig) throws Exception {
        if (lsig.sigCount() > 0) {
            throw new IllegalStateException("LogicsigSignature already has a signature");
        }
        lsig.pqsig = this.sign(lsig.bytesToSignPQ(this.getAddress()));
        return lsig;
    }
}
