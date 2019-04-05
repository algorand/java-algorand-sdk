package com.algorand.algosdk.util;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Digester {
    // Used to select hash/digest algorithm from provider
    private static final String SHA256_ALG = "SHA-512/256";

    public static byte[] digest(byte[] data) throws NoSuchAlgorithmException {
        java.security.MessageDigest digest = java.security.MessageDigest.getInstance(SHA256_ALG);
        digest.update(Arrays.copyOf(data, data.length));
        return digest.digest();
    }
}
