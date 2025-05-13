package com.algorand.algosdk.mnemonic;

import org.junit.jupiter.api.Test;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TestMnemonic {
    @Test
    public void testZeroVector() throws Exception {
        byte[] zeroKeys = new byte[32];
        String expectedMn = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon invest";
        String mn = Mnemonic.fromKey(zeroKeys);
        assertThat(mn).isEqualTo(expectedMn);
        byte[] goBack = Mnemonic.toKey(mn);
        assertThat(goBack).isEqualTo(zeroKeys);
    }

    @Test
    public void testWordNotInList() throws Exception {
        String mn = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon zzz invest";
        assertThatThrownBy(() -> Mnemonic.toKey(mn))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("mnemonic contains word that is not in word list");
    }

    @Test
    public void testGenerateAndRecovery() throws Exception {
        Random r = new Random();
        for (int i = 0; i < 1000; i++) {
            byte[] randKey = new byte[32];
            r.nextBytes(randKey);
            String mn = Mnemonic.fromKey(Arrays.copyOf(randKey, randKey.length));
            byte[] regenKey = Mnemonic.toKey(mn);
            assertThat(regenKey).isEqualTo(randKey);
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
            String oldWord = words[words.length - 1];
            String newWord = oldWord;
            while (oldWord.equals(newWord)) {
                newWord = Wordlist.RAW[r.nextInt(2^11)];
            }
            words[words.length - 1] = newWord;

            StringBuilder s = new StringBuilder();
            for (int j = 0; j < words.length; j++) {
                if (j > 0) s.append(" ");
                s.append(words[j]);
            }
            String corruptedMn = s.toString();
            assertThatThrownBy(() -> Mnemonic.toKey(corruptedMn))
                    .isInstanceOf(GeneralSecurityException.class)
                    .hasMessage("checksum failed to validate");
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
            assertThatThrownBy(() -> Mnemonic.fromKey(randKey))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("key length must be 32 bytes");
        }
    }

}
