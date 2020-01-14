package com.algorand.algosdk.account;


import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.Arrays;

import com.algorand.algosdk.auction.Bid;
import com.algorand.algosdk.auction.SignedBid;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Ed25519PublicKey;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.crypto.MultisigAddress;
import com.algorand.algosdk.crypto.MultisigSignature;
import com.algorand.algosdk.crypto.MultisigSignature.MultisigSubsig;
import com.algorand.algosdk.crypto.Signature;
import com.algorand.algosdk.mnemonic.Mnemonic;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.CryptoProvider;
import com.algorand.algosdk.util.Encoder;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;

/**
 * Create and manage secrets, and perform account-based work such as signing transactions.
 */
public class Account {
    private final KeyPair privateKeyPair;
    private final Address address;
    private static final String KEY_ALGO = "Ed25519";
    private static final String SIGN_ALGO = "EdDSA";
    private static final int PK_SIZE = 32;
    private static final int PK_X509_PREFIX_LENGTH = 12; // Ed25519 specific
    private static final int SK_SIZE = 32;
    private static final int SK_SIZE_BITS = SK_SIZE * 8;
    private static final byte[] BID_SIGN_PREFIX = ("aB").getBytes(StandardCharsets.UTF_8);
    private static final byte[] BYTES_SIGN_PREFIX = ("MX").getBytes(StandardCharsets.UTF_8);
    public static final BigInteger MIN_TX_FEE_UALGOS = BigInteger.valueOf(1000);

    /**
     * Account creates a new, random account.
     */
    public Account() throws NoSuchAlgorithmException {
        this((SecureRandom)null);
    }

    /**
     * Create a new account from an rfc8037 private key.
     * @param seed
     * @throws NoSuchAlgorithmException
     */
    public Account(byte[] seed) throws NoSuchAlgorithmException {
        // seed here corresponds to rfc8037 private key, corresponds to seed in go impl
        // BC for instance takes the seed as private key straight up
        this(new FixedSecureRandom(seed));
    }

    public Account(String mnemonic) throws GeneralSecurityException {
        this(Mnemonic.toKey(mnemonic));
    }

    // randomSrc can be null, in which case system default is used
    private Account(SecureRandom randomSrc) throws NoSuchAlgorithmException {
        CryptoProvider.setupIfNeeded();
        KeyPairGenerator gen = KeyPairGenerator.getInstance(KEY_ALGO);
        if (randomSrc != null) {
            gen.initialize(SK_SIZE_BITS, randomSrc);
        }
        this.privateKeyPair = gen.generateKeyPair();
        // now, convert public key to an address
        byte[] raw = this.getClearTextPublicKey();
        this.address = new Address(Arrays.copyOf(raw, raw.length));
    }

    /**
     * Convenience method for getting the underlying public key for raw operations.
     * @return the public key as length 32 byte array.
     */
    public byte[] getClearTextPublicKey() {
        byte[] b = this.privateKeyPair.getPublic().getEncoded(); // X.509 prepended with ASN.1 prefix
        if (b.length != PK_SIZE + PK_X509_PREFIX_LENGTH) {
            throw new RuntimeException("Generated public key and X.509 prefix is the wrong size");
        }
        byte[] raw = new byte[PK_SIZE];
        System.arraycopy(b, PK_X509_PREFIX_LENGTH, raw, 0, PK_SIZE);
        return raw;
    }

    public Ed25519PublicKey getEd25519PublicKey() {
        return new Ed25519PublicKey(this.getClearTextPublicKey());
    }

    /**
     * Convenience method for getting underlying address.
     * @return account address
     */
    public Address getAddress() {
        return this.address;
    }

    /**
     * Converts the 32 byte private key to a 25 word mnemonic, including a checksum.
     * Refer to the mnemonic package for additional documentation.
     * @return string a 25 word mnemonic
     */
    public String toMnemonic() {
        // this is the only place we use a bouncy castle compile-time dependency
        byte[] X509enc = this.privateKeyPair.getPrivate().getEncoded();
        PrivateKeyInfo pkinfo = PrivateKeyInfo.getInstance(X509enc);
        try {
            ASN1Encodable keyOcts = pkinfo.parsePrivateKey();
            byte[] res = ASN1OctetString.getInstance(keyOcts).getOctets();
            return Mnemonic.fromKey(res);
        } catch (IOException e) {
            throw new RuntimeException("unexpected behavior", e);
        }
    }

    /**
     * Sign a transaction with this account
     * @param tx the transaction to sign
     * @return a signed transaction
     * @throws NoSuchAlgorithmException if signing algorithm could not be found
     */
    public SignedTransaction signTransaction(Transaction tx) throws NoSuchAlgorithmException {
        try {
            byte[] prefixEncodedTx = tx.bytesToSign();
            Signature txSig = rawSignBytes(Arrays.copyOf(prefixEncodedTx, prefixEncodedTx.length));
            return new SignedTransaction(tx, txSig, tx.txID());
        } catch (IOException e) {
            throw new RuntimeException("unexpected behavior", e);
        }
    }

    /**
     * Sign a canonical msg-pack encoded Transaction
     * @param bytes a canonical msg-pack encoded transaction
     * @return a signed transaction
     * @throws NoSuchAlgorithmException if ed25519 not found on this system
     */
    public SignedTransaction signTransactionBytes(byte[] bytes) throws NoSuchAlgorithmException, IOException {
        try {
            Transaction tx = Encoder.decodeFromMsgPack(bytes, Transaction.class);
            return this.signTransaction(tx);
        } catch (IOException e) {
            throw new IOException("could not decode transaction", e);
        }
    }

    /**
     * Sign a transaction with this account, replacing the fee with the given feePerByte.
     * @param tx transaction to sign
     * @param feePerByte fee per byte, often returned as a suggested fee
     * @return a signed transaction
     * @throws NoSuchAlgorithmException crypto provider not found
     */
    public SignedTransaction signTransactionWithFeePerByte(Transaction tx, BigInteger feePerByte) throws NoSuchAlgorithmException {
        setFeeByFeePerByte(tx, feePerByte);
        return this.signTransaction(tx);
    }

    /**
     * Sign a bid with this account
     * @param bid the bid to sign
     * @return a signed bid
     */
    public SignedBid signBid(Bid bid) throws NoSuchAlgorithmException {
        try {
            byte[] encodedBid = Encoder.encodeToMsgPack(bid);
            // prepend hashable prefix
            byte[] prefixEncodedBid = new byte[encodedBid.length + BID_SIGN_PREFIX.length];
            System.arraycopy(BID_SIGN_PREFIX, 0, prefixEncodedBid, 0, BID_SIGN_PREFIX.length);
            System.arraycopy(encodedBid, 0, prefixEncodedBid, BID_SIGN_PREFIX.length, encodedBid.length);
            // sign
            Signature bidSig = rawSignBytes(prefixEncodedBid);
            return new SignedBid(bid, bidSig);
        } catch (IOException e) {
            throw new RuntimeException("unexpected behavior", e);
        }
    }

    /**
     * Creates a version of the given transaction with fee populated according to suggestedFeePerByte * estimateTxSize.
     * @param copyTx transaction to populate fee field
     * @param suggestedFeePerByte suggestedFee given by network
     * @return transaction with proper fee set
     * @throws NoSuchAlgorithmException could not estimate tx encoded size.
     * @deprecated  Replaced by {@link #setFeeByFeePerByte}.
     * This is unsafe to use because the returned transaction is a shallow copy of copyTx.
     */
    @Deprecated
    static public Transaction transactionWithSuggestedFeePerByte(Transaction copyTx, BigInteger suggestedFeePerByte) throws NoSuchAlgorithmException{
        BigInteger newFee = suggestedFeePerByte.multiply(estimatedEncodedSize(copyTx));
        if (newFee.compareTo(MIN_TX_FEE_UALGOS) < 0) {
            newFee = MIN_TX_FEE_UALGOS;
        }
        switch (copyTx.type) {
            case Payment:
                return new Transaction(copyTx.sender, newFee, copyTx.firstValid, copyTx.lastValid, copyTx.note, copyTx.genesisID, copyTx.genesisHash,
                        copyTx.amount, copyTx.receiver, copyTx.closeRemainderTo);
            case KeyRegistration:
                return new Transaction(copyTx.sender, newFee, copyTx.firstValid, copyTx.lastValid, copyTx.note, copyTx.genesisID, copyTx.genesisHash,
                        copyTx.votePK, copyTx.selectionPK, copyTx.voteFirst, copyTx.voteLast, copyTx.voteKeyDilution);
            case Default:
                throw new IllegalArgumentException("tx cannot have no type");
            default:
                throw new RuntimeException("cannot reach");
        }
    }
    
    /**
     * Sets the transaction fee according to suggestedFeePerByte * estimateTxSize.
     * @param tx transaction to populate fee field
     * @param suggestedFeePerByte suggestedFee given by network
     * @throws NoSuchAlgorithmException could not estimate tx encoded size.
     */
    static public void setFeeByFeePerByte(Transaction tx, BigInteger suggestedFeePerByte) throws NoSuchAlgorithmException{
        BigInteger size = estimatedEncodedSize(tx);
        BigInteger newFee = suggestedFeePerByte.multiply(size);
        if (newFee.compareTo(MIN_TX_FEE_UALGOS) < 0) {
            newFee = MIN_TX_FEE_UALGOS;
        }
        tx.fee = newFee;
    }

    /**
     * EstimateEncodedSize returns the estimated encoded size of the transaction including the signature.
     * This function is useful for calculating the fee from suggested fee per byte.
     * @return an estimated byte size for the transaction.
     */
    public static BigInteger estimatedEncodedSize(Transaction tx) throws NoSuchAlgorithmException {
        Account acc = new Account();
        try {
            return BigInteger.valueOf(Encoder.encodeToMsgPack(acc.signTransaction(tx)).length);
        } catch (IOException e) {
            throw new RuntimeException("unexpected behavior", e);
        }
    }

    /**
     * Sign the given bytes, and wrap in Signature.
     * @param bytes the data to sign
     * @return a signature
     */
    private Signature rawSignBytes(byte[] bytes) throws NoSuchAlgorithmException {
        try {
            CryptoProvider.setupIfNeeded();
            java.security.Signature signer = java.security.Signature.getInstance(SIGN_ALGO);
            signer.initSign(this.privateKeyPair.getPrivate());
            signer.update(bytes);
            byte[] sigRaw = signer.sign();
            return new Signature(sigRaw);
        } catch (InvalidKeyException|SignatureException e) {
            throw new RuntimeException("unexpected behavior", e);
        }
    }

    /**
     * Sign the given bytes, and wrap in signature. The message is prepended with "MX" for domain separation.
     * @param bytes the data to sign
     * @return a signature
     */
    public Signature signBytes(byte[] bytes) throws NoSuchAlgorithmException {
        // prepend hashable prefix
        byte[] prefixBytes = new byte[bytes.length + BYTES_SIGN_PREFIX.length];
        System.arraycopy(BYTES_SIGN_PREFIX, 0, prefixBytes, 0, BYTES_SIGN_PREFIX.length);
        System.arraycopy(bytes, 0, prefixBytes, BYTES_SIGN_PREFIX.length, bytes.length);
        // sign
        return rawSignBytes(prefixBytes);
    }

    /* Multisignature support */

    /**
     * signMultisigTransaction creates a multisig transaction from the input and the multisig account.
     * @param from sign as this multisignature account
     * @param tx the transaction to sign
     * @return SignedTransaction a partially signed multisig transaction
     * @throws NoSuchAlgorithmException if could not sign tx
     */
    public SignedTransaction signMultisigTransaction(MultisigAddress from, Transaction tx) throws NoSuchAlgorithmException {
        // check that from addr of tx matches multisig preimage
        if (!tx.sender.toString().equals(from.toString())) {
            throw new IllegalArgumentException("Transaction sender does not match multisig account");
        }
        // check that account secret key is in multisig pk list
        Ed25519PublicKey myPK = this.getEd25519PublicKey();
        int myI = from.publicKeys.indexOf(myPK);
        if (myI == -1) {
            throw new IllegalArgumentException("Multisig account does not contain this secret key");
        }
        // now, create the multisignature
        SignedTransaction txSig = this.signTransaction(tx);
        MultisigSignature mSig = new MultisigSignature(from.version, from.threshold);
        for (int i = 0; i < from.publicKeys.size(); i++) {
            if (i == myI) {
                mSig.subsigs.add(new MultisigSubsig(myPK, txSig.sig));
            } else {
                mSig.subsigs.add(new MultisigSubsig(from.publicKeys.get(i)));
            }
        }
        return new SignedTransaction(tx, mSig, txSig.transactionID);
    }

    /**
     * mergeMultisigTransactions merges the given (partially) signed multisig transactions.
     * @param txs partially signed multisig transactions to merge. Underlying transactions may be mutated.
     * @return a merged multisig transaction
     */
    public static SignedTransaction mergeMultisigTransactions(SignedTransaction... txs) {
        if (txs.length < 2) {
            throw new IllegalArgumentException("cannot merge a single transaction");
        }
        SignedTransaction merged = txs[0];
        for (int i = 0; i < txs.length; i++) {
            // check that multisig parameters match
            SignedTransaction tx = txs[i];
            if (tx.mSig.version != merged.mSig.version ||
                    tx.mSig.threshold != merged.mSig.threshold) {
                throw new IllegalArgumentException("transaction msig parameters do not match");
            }
            for (int j = 0; j < tx.mSig.subsigs.size(); j++) {
                MultisigSubsig myMsig = merged.mSig.subsigs.get(j);
                MultisigSubsig theirMsig = tx.mSig.subsigs.get(j);
                if (!theirMsig.key.equals(myMsig.key)) {
                    throw new IllegalArgumentException("transaction msig public keys do not match");
                }
                if (myMsig.sig.equals(new Signature())) {
                    myMsig.sig = theirMsig.sig;
                } else if (!myMsig.sig.equals(theirMsig.sig) &&
                        !theirMsig.sig.equals(new Signature())) {
                    throw new IllegalArgumentException("transaction msig has mismatched signatures");
                }
                merged.mSig.subsigs.set(j, myMsig);
            }
        }
        return merged;
    }

    /**
     * appendMultisigTransaction appends our signature to the given multisig transaction.
     * @param from the multisig public identity we are signing for
     * @param signedTx the partially signed msig tx to which to append signature
     * @return a merged multisig transaction
     * @throws NoSuchAlgorithmException unknown signature algorithm
     */
    public SignedTransaction appendMultisigTransaction(MultisigAddress from, SignedTransaction signedTx) throws NoSuchAlgorithmException {
        SignedTransaction sTx = this.signMultisigTransaction(from, signedTx.tx);
        return mergeMultisigTransactions(sTx, signedTx);
    }


    /**
     * mergeMultisigTransactionBytes is a convenience method for working directly with raw transaction files.
     * @param txsBytes list of multisig transactions to merge
     * @return an encoded, merged multisignature transaction
     * @throws NoSuchAlgorithmException if could not compute signature
     */
    public static byte[] mergeMultisigTransactionBytes(byte[]... txsBytes) throws NoSuchAlgorithmException, IOException {
        try {
            SignedTransaction[] sTxs = new SignedTransaction[txsBytes.length];
            for (int i = 0; i < txsBytes.length; i++) {
                sTxs[i] = Encoder.decodeFromMsgPack(txsBytes[i], SignedTransaction.class);
            }
            SignedTransaction merged = Account.mergeMultisigTransactions(sTxs);
            return Encoder.encodeToMsgPack(merged);
        } catch (IOException e) {
            throw new IOException("could not decode transactions", e);
        }
    }

    /**
     * appendMultisigTransactionBytes is a convenience method for directly appending our signature to a raw tx file.
     * @param from the public identity we are signing as.
     * @param txBytes the multisig transaction to append signature to
     * @return merged multisignature transaction inclukding our signature
     * @throws NoSuchAlgorithmException on failure to compute signature
     */
    public byte[] appendMultisigTransactionBytes(MultisigAddress from, byte[] txBytes) throws NoSuchAlgorithmException, IOException {
        try {
            SignedTransaction inTx = Encoder.decodeFromMsgPack(txBytes, SignedTransaction.class);
            SignedTransaction appended = this.appendMultisigTransaction(from, inTx);
            return Encoder.encodeToMsgPack(appended);
        } catch (IOException e) {
            throw new IOException("could not decode transactions", e);
        }
    }

    /**
     * signMultisigTransactionBytes is a convenience method for signing a multistransaction into bytes
     * @param from the public identity we are signing as.
     * @param tx the multisig transaction to append signature to
     * @return merged multisignature transaction inclukding our signature
     * @throws NoSuchAlgorithmException on failure to compute signature
     */
    public byte[] signMultisigTransactionBytes(MultisigAddress from, Transaction tx) throws NoSuchAlgorithmException, IOException {
        try {
            SignedTransaction signed = this.signMultisigTransaction(from, tx);
            return Encoder.encodeToMsgPack(signed);
        } catch (IOException e) {
            throw new IOException("could not encode transactions", e);
        }
    }

    /**
     * Sign LogicSig with account's secret key
     * @param lsig LogicsigSignature to sign
     * @return LogicsigSignature with updated signature
     * @throws IOException
     */
    public LogicsigSignature signLogicsig(LogicsigSignature lsig) throws IOException {
        Signature sig;
        try {
            byte[] bytesToSign = lsig.bytesToSign();
            sig = this.rawSignBytes(bytesToSign);
        } catch (NoSuchAlgorithmException ex) {
            throw new IOException("could not sign transaction", ex);
        }
        lsig.sig = sig;
        return lsig;
    }

    /**
     * Sign LogicSig as multisig
     * @param lsig LogicsigSignature to sign
     * @param ma MultisigAddress to format multi signature from
     * @return LogicsigSignature
     * @throws IOException
     */
    public LogicsigSignature signLogicsig(LogicsigSignature lsig, MultisigAddress ma) throws IOException {
        Ed25519PublicKey myPK = this.getEd25519PublicKey();
        int myIndex = ma.publicKeys.indexOf(myPK);
        if (myIndex == -1) {
            throw new IllegalArgumentException("Multisig account does not contain this secret key");
        }
        // now, create the multisignature
        Signature sig;
        try {
            byte[] bytesToSign = lsig.bytesToSign();
            sig = this.rawSignBytes(bytesToSign);
        } catch (NoSuchAlgorithmException ex) {
            throw new IOException("could not sign transaction", ex);
        }

        MultisigSignature mSig = new MultisigSignature(ma.version, ma.threshold);
        for (int i = 0; i < ma.publicKeys.size(); i++) {
            if (i == myIndex) {
                mSig.subsigs.add(new MultisigSubsig(myPK, sig));
            } else {
                mSig.subsigs.add(new MultisigSubsig(ma.publicKeys.get(i)));
            }
        }
        lsig.msig = mSig;
        return lsig;
    }

    /**
     * Appends a signature to multisig logic signed transaction
     * @param lsig LogicsigSignature append to
     * @return LogicsigSignature
     * @throws IllegalArgumentException
     * @throws NoSuchAlgorithmException
     */
    public LogicsigSignature appendToLogicsig(LogicsigSignature lsig) throws IllegalArgumentException, IOException {
        Ed25519PublicKey myPK = this.getEd25519PublicKey();
        int myIndex = -1;
        for (int i = 0; i < lsig.msig.subsigs.size(); i++ ) {
            MultisigSubsig subsig = lsig.msig.subsigs.get(i);
            if (subsig.key.equals(myPK)) {
                myIndex = i;
            }
        }
        if (myIndex == -1) {
            throw new IllegalArgumentException("Multisig account does not contain this secret key");
        }

        try {
            // now, create the multisignature
            byte[] bytesToSign = lsig.bytesToSign();
            Signature sig = this.rawSignBytes(bytesToSign);
            lsig.msig.subsigs.set(myIndex, new MultisigSubsig(myPK, sig));
            return lsig;
        } catch (NoSuchAlgorithmException ex) {
            throw new IOException("could not sign transaction", ex);
        }

    }

    /**
     * Creates SignedTransaction from LogicsigSignature and Transaction.
     * LogicsigSignature must be valid and verifiable against transaction sender field.
     * @param lsig LogicsigSignature
     * @param tx Transaction
     * @return SignedTransaction
     */
    public static SignedTransaction signLogicsigTransaction(LogicsigSignature lsig, Transaction tx) throws IllegalArgumentException, IOException {
        if (!lsig.verify(tx.sender)) {
            throw new IllegalArgumentException("verification failed");
        }

        try {
            return new SignedTransaction(tx, lsig, tx.txID());
        } catch (Exception ex) {
            throw new IOException("could not encode transactions", ex);
        }
    }

    // Return a pre-set seed in response to nextBytes or generateSeed
    private static class FixedSecureRandom extends SecureRandom {
        private final byte[] fixedValue;
        private int index = 0;

        public FixedSecureRandom(byte[] fixedValue) {
            this.fixedValue = Arrays.copyOf(fixedValue, fixedValue.length);
        }

        @Override
        public void nextBytes(byte[] bytes) {
            if (this.index >= this.fixedValue.length) {
                // no more data to copy
                return;
            }
            int len = bytes.length;
            if (len > this.fixedValue.length - this.index) {
                len = this.fixedValue.length - this.index;
            }
            System.arraycopy(this.fixedValue, this.index, bytes, 0, len);
            this.index += bytes.length;
        }

        @Override
        public byte[] generateSeed(int numBytes) {
            byte[] bytes = new byte[numBytes];
            this.nextBytes(bytes);
            return bytes;
        }
    }

}
