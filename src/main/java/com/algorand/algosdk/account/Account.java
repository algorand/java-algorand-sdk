package com.algorand.algosdk.account;


import com.algorand.algosdk.auction.Bid;
import com.algorand.algosdk.auction.SignedBid;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Signature;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Objects;

/**
 * Create and manage secrets, and perform account-based work such as signing transactions.
 */
public class Account {
    private final KeyPair privateKeyPair;
    private final Address address;
    private static final String KEY_ALGO = "Ed25519";
    private static final String SIGN_ALGO = "EdDSA";
    private static final int PK_SIZE = 32;
    private static final int SK_SIZE = 32;
    private static final byte[] TX_SIGN_PREFIX = ("TX").getBytes(StandardCharsets.UTF_8);
    private static final byte[] BID_SIGN_PREFIX = ("aB").getBytes(StandardCharsets.UTF_8);

    /**
     * Account creates a new, random account.
     */
    public Account() throws GeneralSecurityException {
        this((SecureRandom)null);
    }

    /**
     * Create a new account from an existing PKCS8 encoded keypair and Algorand address.
     * @param secretKey existing private key. Must be 32 bytes. TODO what format? ASN.1??
     * @param publicKey existing public key. Must be 32 bytes.
     * @param address existing address. Must be same size as public key.
     */
    public Account(byte[] secretKey, byte[] publicKey, Address address) throws Exception {
        Objects.requireNonNull(secretKey, "secret key must not be null");
        Objects.requireNonNull(publicKey, "public key must not be null");
        this.address = Objects.requireNonNull(address, "address must not be null");

        if (publicKey.length != PK_SIZE || secretKey.length != SK_SIZE) {
            throw new GeneralSecurityException("Input keys are the wrong size");
        }
        PKCS8EncodedKeySpec pkS = new PKCS8EncodedKeySpec(publicKey);
        PKCS8EncodedKeySpec skS = new PKCS8EncodedKeySpec(secretKey);

        // JCA keypair
        KeyFactory kf = KeyFactory.getInstance(KEY_ALGO);
        PublicKey pk = kf.generatePublic(pkS);
        PrivateKey sk = kf.generatePrivate(skS);
        this.privateKeyPair = new KeyPair(pk, sk);
    }

    public Account(byte[] seed) throws GeneralSecurityException {
        // seed here corresponds to rfc8037 private key, corresponds to seed in go impl
        // BC for instance takes the seed as private key straight up
        this(new FixedSecureRandom(seed));
    }

    // randomSrc can be null, in which case system default is used
    private Account(SecureRandom randomSrc) throws GeneralSecurityException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance(KEY_ALGO);
        if (randomSrc != null) {
            gen.initialize(SK_SIZE, randomSrc);
        }
        this.privateKeyPair = gen.generateKeyPair();
        // now, convert public key to an address
        byte[] b = this.privateKeyPair.getPublic().getEncoded();
        if (b.length != PK_SIZE) {
            throw new GeneralSecurityException("Generated public key is the wrong size");
        }
        this.address = new Address(Arrays.copyOf(b, b.length));
    }

    /**
     * Convenience method for getting the underlying public key for raw operations.
     * @return the public key as length 32 byte array.
     */
    public byte[] getClearTextPublicKey() {
        return this.privateKeyPair.getPublic().getEncoded();
    }

    /**
     * Convenience method for getting underlying private key for raw operations.
     * @return the private key as length 32 byte array.
     */
    public byte[] getClearTextPrivateKey() {
        return this.privateKeyPair.getPrivate().getEncoded();
    }

    /**
     * Converts the 32 byte private key to a 25 word mnemonic, including a checksum.
     * Refer to the mnemonic package for additional documentation.
     * @return string a 25 word mnemonic
     */
    public String toMnemonic() {
        return "";
    }

    /**
     * Sign a transaction with this account
     * @param tx the transaction to sign
     * @return a signed transaction
     */
    public SignedTransaction signTransaction(Transaction tx) throws Exception {
        byte[] encodedTx = Encoder.encode(tx);
        // prepend hashable prefix
        byte[] prefixEncodedTx = new byte[encodedTx.length + TX_SIGN_PREFIX.length];
        System.arraycopy(TX_SIGN_PREFIX, 0, prefixEncodedTx, 0, TX_SIGN_PREFIX.length);
        System.arraycopy(encodedTx, 0, prefixEncodedTx, TX_SIGN_PREFIX.length, encodedTx.length);
        // sign
        Signature txSig = signBytes(prefixEncodedTx);
        return new SignedTransaction(tx, txSig);
    }

    /**
     * Sign a bid with this account
     * @param bid the bid to sign
     * @return a signed bid
     */
    public SignedBid signBid(Bid bid) throws Exception {
        byte[] encodedBid = Encoder.encode(bid);
        // prepend hashable prefix
        byte[] prefixEncodedBid = new byte[encodedBid.length + BID_SIGN_PREFIX.length];
        System.arraycopy(BID_SIGN_PREFIX, 0, prefixEncodedBid, 0, BID_SIGN_PREFIX.length);
        System.arraycopy(encodedBid, 0, prefixEncodedBid, BID_SIGN_PREFIX.length, encodedBid.length);
        // sign
        Signature bidSig = signBytes(prefixEncodedBid);
        return new SignedBid(bid, bidSig);
    }


    /**
     * Sign the given bytes, and wrap in Signature.
     * @param bytes the data to sign
     * @return a signature
     */
    private Signature signBytes(byte[] bytes) throws GeneralSecurityException {
        java.security.Signature signer = java.security.Signature.getInstance(SIGN_ALGO);
        signer.initSign(this.privateKeyPair.getPrivate());
        signer.update(bytes);
        byte[] sigRaw = signer.sign();
        Signature sig = new Signature(sigRaw);
        return sig;
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
