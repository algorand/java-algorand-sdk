package com.algorand.algosdk.signer;

import com.algorand.algosdk.crypto.PQSignature;

import java.security.NoSuchAlgorithmException;

/**
 * A {@link com.algorand.algosdk.transaction.TxnSigner} backed by a Falcon-1024
 * post-quantum signing callback ({@link PQSignature#falcon1024Scheme()})
 * instead of a raw secret key. Besides signing transactions it can delegate a
 * logic signature via {@code signLogicsig}. The two-argument constructor signs
 * for the canonical-salt address derived from the public key; the three-argument
 * constructor accepts an explicit salt.
 * <p>
 * The SDK does not bundle a Falcon-1024 implementation: the caller supplies it
 * as the {@link RawSigner} callback, which receives the full domain-separated
 * signing preimage ("TX" + txn for transactions, "PQProgram" + address +
 * program for delegated logic signatures) and returns the raw detached
 * Falcon-1024 signature.
 */
public class Falcon1024AlgorandSigner extends PQAlgorandSigner {
    /**
     * @param publicKey the Falcon-1024 public key
     * @param signer callback that returns a raw Falcon-1024 signature over the
     *               given preimage bytes
     * @throws NoSuchAlgorithmException if SHA-512/256 is unavailable
     */
    public Falcon1024AlgorandSigner(byte[] publicKey, RawSigner signer) throws NoSuchAlgorithmException {
        super(publicKey, signer, PQSignature.falcon1024Scheme());
    }

    /**
     * Create a signer for an explicit, possibly non-canonical salt. A Falcon-1024
     * public key has an address for every salt whose digest lands off the ed25519
     * curve, and consensus resolves the authorizer from the salt the signature
     * carries, so all of them are usable accounts.
     * @param publicKey the Falcon-1024 public key
     * @param signer callback that returns a raw Falcon-1024 signature over the
     *               given preimage bytes
     * @param salt the salt to sign for (0-255)
     * @throws IllegalArgumentException if the salt is out of range
     * @throws NoSuchAlgorithmException if SHA-512/256 is unavailable
     */
    public Falcon1024AlgorandSigner(byte[] publicKey, RawSigner signer, int salt) throws NoSuchAlgorithmException {
        super(publicKey, signer, PQSignature.falcon1024Scheme(), salt);
    }
}
