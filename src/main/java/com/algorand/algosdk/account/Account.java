package com.algorand.algosdk.account;


import com.algorand.algosdk.crypto.Address;

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
    private static final int PK_SIZE = 32;
    private static final int SK_SIZE = 32;

    /**
     * Account creates a new, random account.
     */
    public Account() throws GeneralSecurityException {
        // generate random keypair
        this.privateKeyPair = KeyPairGenerator.getInstance(KEY_ALGO).generateKeyPair();

        // now, convert public key to an address
        byte[] b = this.privateKeyPair.getPublic().getEncoded();
        if (b.length != PK_SIZE) {
            throw new GeneralSecurityException("Generated public key is the wrong size");
        }
        this.address = new Address(Arrays.copyOf(b, b.length));
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

}
