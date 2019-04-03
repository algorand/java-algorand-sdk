package com.algorand.algosdk.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Digest {
    // Used to select hash/digest algorithm from provider
    private static final String SHA256_ALG = "SHA-512/256";

    public static byte[] digest(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(SHA256_ALG);
        digest.update(Arrays.copyOf(data, data.length));
        return digest.digest();
    }
}
