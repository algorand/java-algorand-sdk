package com.algorand.algosdk.mnemonic;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.Arrays;
import java.util.Random;

public class TestMnemonic {
    @BeforeClass
    public static void setup() {
        // add bouncy castle provider for crypto
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    @Test
    public void testZeroVector() throws Exception {
        byte[] zeroKeys = new byte[32];
        String expectedMn = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon invest";
        String mn = Mnemonic.fromKey(zeroKeys);
        Assert.assertEquals(expectedMn, mn);
        byte[] goBack = Mnemonic.toKey(mn);
        Assert.assertArrayEquals(zeroKeys, goBack);
    }

    @Test
    public void testWordNotInList() throws Exception {
        String mn = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon zzz invest";
        try {
            byte[] keyBytes = Mnemonic.toKey(mn);
            Assert.fail("Did not fail on invalid mnemonic");
        } catch (IllegalArgumentException e) {
            return;
        }
    }

    @Test
    public void testGenerateAndRecovery() throws Exception {
        Random r = new Random();
        for (int i = 0; i < 1000; i++) {
            byte[] randKey = new byte[32];
            r.nextBytes(randKey);
            String mn = Mnemonic.fromKey(Arrays.copyOf(randKey, randKey.length));
            byte[] regenKey = Mnemonic.toKey(mn);
            Assert.assertArrayEquals(randKey, regenKey);
        }
    }

    @Test
    public void testCorruptedChecksum() throws Exception {
        Random r = new Random();
        for (int i = 0; i < 1000; i++) {
            byte[] randKey = new byte[32];
            r.nextBytes(randKey);
            String mn = Mnemonic.fromKey(Arrays.copyOf(randKey, randKey.length));
            String[] words = mn.split(" ");
            words[words.length - 1] = Wordlist.RAW[r.nextInt(2^11)];

            StringBuilder s = new StringBuilder();
            for (int j = 0; j < words.length; j++) {
                if (j > 0) s.append(" ");
                s.append(words[j]);
            }
            String corruptedMn = s.toString();
            try {
                byte[] recKey = Mnemonic.toKey(corruptedMn);
                Assert.fail("Corrupted checksum should not have validated");
            } catch (GeneralSecurityException e) {
                // should have failed
                continue;
            }
        }
    }

    @Test
    public void testInvalidKeylen() throws Exception {
        Random r = new Random();
        int[] badLengths = new int[]{
            0, 31, 33, 100, 35, 2, 30
        };
        for (int badlen: badLengths) {
            byte[] randKey = new byte[badlen];
            r.nextBytes(randKey);
            try {
                String mn = Mnemonic.fromKey(randKey);
                Assert.fail("Invalid key length should be illegal argument to mnemonic generator");
            } catch (IllegalArgumentException e) {
                continue;
            }
        }
    }

}
