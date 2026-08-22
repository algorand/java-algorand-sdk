package com.algorand.algosdk.signer;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Ed25519PublicKey;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.crypto.MultisigAddress;
import com.algorand.algosdk.crypto.MultisigSignature;
import com.algorand.algosdk.crypto.MultisigSignature.MultisigSubsig;
import com.algorand.algosdk.crypto.Signature;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.transaction.TxnSigner;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * A {@link TxnSigner} backed by a single low-level ed25519 signing callback (a
 * public key plus a {@link RawSigner} that signs exact bytes) instead of a raw
 * secret key. Besides signing transactions it can also sign messages ("MX"),
 * program data ("ProgData"), and delegate logic signatures — the callback-based
 * counterparts of the {@link com.algorand.algosdk.account.Account} methods.
 * <p>
 * The callback receives the full domain-separated preimage (ed25519 hashes
 * internally) and must return the raw 64-byte detached signature.
 */
public class Ed25519AlgorandSigner implements TxnSigner {
    private static final byte[] BYTES_SIGN_PREFIX = ("MX").getBytes(StandardCharsets.UTF_8);
    private static final byte[] PROGDATA_SIGN_PREFIX = ("ProgData").getBytes(StandardCharsets.UTF_8);

    private final Ed25519PublicKey publicKey;
    private final RawSigner signer;
    private final Address address;

    /**
     * @param publicKey the 32-byte ed25519 public key
     * @param signer callback that signs exact preimage bytes and returns the raw
     *               64-byte signature
     */
    public Ed25519AlgorandSigner(Ed25519PublicKey publicKey, RawSigner signer) {
        Objects.requireNonNull(publicKey, "publicKey must not be null");
        this.publicKey = new Ed25519PublicKey(publicKey.getBytes());
        this.signer = Objects.requireNonNull(signer, "signer must not be null");
        this.address = new Address(this.publicKey.getBytes());
    }

    /**
     * @param publicKey the 32-byte ed25519 public key
     * @param signer callback that signs exact preimage bytes and returns the raw
     *               64-byte signature
     */
    public Ed25519AlgorandSigner(byte[] publicKey, RawSigner signer) {
        this(new Ed25519PublicKey(publicKey), signer);
    }

    /**
     * @return the address of this signer's public key
     */
    public Address getAddress() {
        return this.address;
    }

    /**
     * @return this signer's public key
     */
    public Ed25519PublicKey getPublicKey() {
        return this.publicKey;
    }

    // invoke the callback and wrap the raw signature
    Signature rawSign(byte[] bytes) throws Exception {
        return new Signature(this.signer.sign(bytes));
    }

    @Override
    public SignedTransaction[] signTxnGroup(Transaction[] txnGroup, int[] indicesToSign) throws Exception {
        SignedTransaction[] sTxn = new SignedTransaction[indicesToSign.length];
        for (int i = 0; i < indicesToSign.length; i++) {
            Transaction tx = txnGroup[indicesToSign[i]];
            SignedTransaction stx = new SignedTransaction(tx, this.rawSign(tx.bytesToSign()), tx.txID());
            // This signer's own address is the authorizer; attach it as the auth
            // address whenever the transaction is sent by a different (rekeyed)
            // account.
            if (!tx.sender.equals(this.address)) {
                stx.authAddr(this.address);
            }
            sTxn[i] = stx;
        }
        return sTxn;
    }

    /**
     * Sign the given bytes, prepended with "MX" for domain separation: the
     * callback-based counterpart of
     * {@link com.algorand.algosdk.account.Account#signBytes}. The returned
     * signature verifies with {@link Address#verifyBytes}.
     * @param bytes the data to sign
     * @return a signature
     */
    public Signature signBytes(byte[] bytes) throws Exception {
        byte[] prefixBytes = new byte[bytes.length + BYTES_SIGN_PREFIX.length];
        System.arraycopy(BYTES_SIGN_PREFIX, 0, prefixBytes, 0, BYTES_SIGN_PREFIX.length);
        System.arraycopy(bytes, 0, prefixBytes, BYTES_SIGN_PREFIX.length, bytes.length);
        return this.rawSign(prefixBytes);
    }

    /**
     * Creates Signature compatible with ed25519verify TEAL opcode from data and
     * contract address (program hash): the callback-based counterpart of
     * {@link com.algorand.algosdk.account.Account#tealSign}.
     * @param data byte[]
     * @param contractAddress Address
     * @return Signature
     */
    public Signature tealSign(byte[] data, Address contractAddress) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(PROGDATA_SIGN_PREFIX);
        baos.write(contractAddress.getBytes());
        baos.write(data);
        return this.rawSign(baos.toByteArray());
    }

    /**
     * Creates Signature compatible with ed25519verify TEAL opcode from data and
     * program bytes: the callback-based counterpart of
     * {@link com.algorand.algosdk.account.Account#tealSignFromProgram}.
     * @param data byte[]
     * @param program byte[]
     * @return Signature
     */
    public Signature tealSignFromProgram(byte[] data, byte[] program) throws Exception {
        LogicsigSignature lsig = new LogicsigSignature(program);
        return this.tealSign(data, lsig.toAddress());
    }

    /**
     * Two signers for the same public key are equal, whatever callback backs
     * them. This lets
     * {@link com.algorand.algosdk.transaction.AtomicTransactionComposer} collapse
     * equivalent signers into one signing call.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Ed25519AlgorandSigner)) return false;
        return this.publicKey.equals(((Ed25519AlgorandSigner) obj).publicKey);
    }

    @Override
    public int hashCode() {
        return this.publicKey.hashCode();
    }

    /**
     * Sign a transaction as one member of a multisig account: the callback-based
     * counterpart of
     * {@link com.algorand.algosdk.account.Account#signMultisigTransaction}.
     * The other members' slots are left unsigned, ready to be filled by
     * {@link #appendToMultisigTransaction} or
     * {@link com.algorand.algosdk.account.Account#mergeMultisigTransactions}.
     * @param ma the multisig account to sign on behalf of
     * @param tx the transaction to sign
     * @return the partially-signed multisig transaction
     * @throws IllegalArgumentException if this signer's public key is not a member of the multisig
     */
    public SignedTransaction signMultisigTransaction(MultisigAddress ma, Transaction tx) throws Exception {
        int myIndex = ma.publicKeys.indexOf(this.publicKey);
        if (myIndex == -1) {
            throw new IllegalArgumentException("Multisig account does not contain this public key");
        }
        MultisigSignature mSig = new MultisigSignature(ma.version, ma.threshold);
        for (int i = 0; i < ma.publicKeys.size(); i++) {
            if (i == myIndex) {
                mSig.subsigs.add(new MultisigSubsig(this.publicKey, this.rawSign(tx.bytesToSign())));
            } else {
                mSig.subsigs.add(new MultisigSubsig(ma.publicKeys.get(i)));
            }
        }
        SignedTransaction stx = new SignedTransaction(tx, mSig, tx.txID());
        // if the transaction sender address is not the multisig address, set the
        // auth address to the multisig address
        Address msigAddr = ma.toAddress();
        stx.authAddr(msigAddr);
        return stx;
    }

    /**
     * Append this signer's subsignature to an already partially-signed multisig
     * transaction, returning the merged result: the callback-based counterpart of
     * {@link com.algorand.algosdk.account.Account#appendMultisigTransaction}.
     * @param ma the multisig account the transaction is signed on behalf of
     * @param signedTx the partially-signed multisig transaction to append to
     * @return the merged multisig transaction
     * @throws IllegalArgumentException if this signer's public key is not a member of the multisig
     */
    public SignedTransaction appendToMultisigTransaction(MultisigAddress ma, SignedTransaction signedTx) throws Exception {
        return Account.mergeMultisigTransactions(this.signMultisigTransaction(ma, signedTx.tx), signedTx);
    }

    /**
     * Sign (delegate) a LogicSig with this signer, in place: the callback-based
     * counterpart of {@link com.algorand.algosdk.account.Account#signLogicsig(LogicsigSignature)}.
     * @param lsig LogicsigSignature to sign
     * @return LogicsigSignature with updated signature
     * @throws IllegalStateException if the LogicSig already carries a signature
     */
    public LogicsigSignature signLogicsig(LogicsigSignature lsig) throws Exception {
        if (lsig.sigCount() > 0) {
            throw new IllegalStateException("LogicsigSignature already has a signature");
        }
        lsig.sig = this.rawSign(lsig.bytesToSign());
        return lsig;
    }

    /**
     * Sign (delegate) a LogicSig as a member of the delegating multisig account,
     * in place: the callback-based counterpart of
     * {@link com.algorand.algosdk.account.Account#signLogicsig(LogicsigSignature, MultisigAddress)}.
     * @param lsig LogicsigSignature to sign
     * @param ma MultisigAddress to format multi signature from
     * @return LogicsigSignature
     * @throws IllegalArgumentException if this signer's public key is not a member of the multisig
     * @throws IllegalStateException if the LogicSig already carries a signature
     */
    public LogicsigSignature signLogicsig(LogicsigSignature lsig, MultisigAddress ma) throws Exception {
        if (lsig.sigCount() > 0) {
            throw new IllegalStateException("LogicsigSignature already has a signature");
        }
        int myIndex = ma.publicKeys.indexOf(this.publicKey);
        if (myIndex == -1) {
            throw new IllegalArgumentException("Multisig account does not contain this public key");
        }
        Signature sig = this.rawSign(lsig.bytesToSignMultisig(ma.toAddress()));
        MultisigSignature mSig = new MultisigSignature(ma.version, ma.threshold);
        for (int i = 0; i < ma.publicKeys.size(); i++) {
            if (i == myIndex) {
                mSig.subsigs.add(new MultisigSubsig(this.publicKey, sig));
            } else {
                mSig.subsigs.add(new MultisigSubsig(ma.publicKeys.get(i)));
            }
        }
        lsig.lmsig = mSig;
        return lsig;
    }

    /**
     * Appends this signer's signature to a multisig-delegated LogicSig, in place:
     * the callback-based counterpart of
     * {@link com.algorand.algosdk.account.Account#appendToLogicsig}.
     * @param lsig LogicsigSignature append to
     * @return LogicsigSignature
     * @throws IllegalArgumentException if the LogicSig is not multisig-delegated,
     *         or this signer's public key is not a member of the delegating multisig
     */
    public LogicsigSignature appendToLogicsig(LogicsigSignature lsig) throws Exception {
        if (lsig.lmsig == null) {
            throw new IllegalArgumentException("LogicsigSignature.lmsig is null; cannot append to multisig logic signature.");
        }
        int myIndex = -1;
        for (int i = 0; i < lsig.lmsig.subsigs.size(); i++) {
            if (lsig.lmsig.subsigs.get(i).key.equals(this.publicKey)) {
                myIndex = i;
            }
        }
        if (myIndex == -1) {
            throw new IllegalArgumentException("Multisig account does not contain this public key");
        }
        Address multisigAddr = lsig.lmsig.convertToMultisigAddress().toAddress();
        Signature sig = this.rawSign(lsig.bytesToSignMultisig(multisigAddr));
        lsig.lmsig.subsigs.set(myIndex, new MultisigSubsig(this.publicKey, sig));
        return lsig;
    }
}
