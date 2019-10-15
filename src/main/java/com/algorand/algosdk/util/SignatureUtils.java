package com.algorand.algosdk.util;
import com.algorand.algosdk.util.CryptoProvider;

import java.security.*;

/**
 * Implements signature utilities such as signing and estimating signed transaction sizes. 
 * Has no dependencies on algosdk packages.
 **/
public class SignatureUtils {
    
    private static final String SIGN_ALGO = "EdDSA";
    private static final String KEY_ALGO = "Ed25519";
    private static final int SK_SIZE = 32;
    private static final int SK_SIZE_BITS = SK_SIZE * 8;

    private static KeyPair dummyKeyPair;

    public static KeyPair generateKeyPair(SecureRandom randomSrc) throws NoSuchAlgorithmException {
	CryptoProvider.setupIfNeeded();
	KeyPairGenerator gen = KeyPairGenerator.getInstance(KEY_ALGO);
	if (randomSrc != null) {
	    gen.initialize(SK_SIZE_BITS, randomSrc);
	}
	return gen.generateKeyPair();
    }
    
    public static KeyPair getDummyPrivateKey() throws NoSuchAlgorithmException {
        if (null == dummyKeyPair) {
            dummyKeyPair = generateKeyPair(null);
        }
        return dummyKeyPair;
    }
    /**
     * Sign the given bytes, and wrap in Signature.
     * @param bytes the data to sign
     * @return a signature
     * @throws NoSuchAlgorithmException 
     */
    public static byte[] rawSignBytes(byte[] bytes, PrivateKey pKey) throws NoSuchAlgorithmException {
	
	try {
            CryptoProvider.setupIfNeeded();
            java.security.Signature signer = java.security.Signature.getInstance(SIGN_ALGO);
            signer.initSign(pKey);
            signer.update(bytes);
            byte[] sigRaw = signer.sign();
            return sigRaw;
        } catch (InvalidKeyException|SignatureException e) {
            throw new RuntimeException("unexpected behavior", e);
        }
    }
}
