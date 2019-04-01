package com.algorand.algosdk.mnemonic;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

/**
 * Provides an easy way to create mnemonics from 32-byte length keys.
 */
public class Mnemonic {

    private static final int BITS_PER_WORD = 11;
    private static final int CHECKSUM_LEN_WORDS = 1;
    private static final int KEY_LEN_BYTES = 32;
    private static final int MNEM_LEN_WORDS = 25; // includes checksum word
    private static final int PADDING_ZEROS = BITS_PER_WORD - ((KEY_LEN_BYTES*8)%BITS_PER_WORD);
    private static final String CHECKSUM_ALG = "SHA-512/256";
    private static final String MNEMONIC_DELIM = " ";

    // on set up, verify expected relationship between constants
    static {
        if (MNEM_LEN_WORDS * BITS_PER_WORD - (CHECKSUM_LEN_WORDS*BITS_PER_WORD) != KEY_LEN_BYTES * 8 + PADDING_ZEROS) {
            throw new RuntimeException("cannot initialize mnemonic library: invalid constants");
        }
    }

    /**
     * Converts a 32-byte key into a 25 word mnemonic. The generated
     * mnemonic includes a checksum. Each word in the mnemonic represents 11 bits
     * of data, and the last 11 bits are reserved for the checksum.
     * @param key 32 byte length key
     * @return
     */
    public static String fromKey(byte[] key) {
        Objects.requireNonNull(key, "key must not be null");
        if (key.length != KEY_LEN_BYTES) {
            throw new IllegalArgumentException("key length must be " + KEY_LEN_BYTES + " bytes");
        }
        String chkWord = checksum(key);
        int[] uint11Arr = toUintNArray(key);
        String[] words = applyWords(uint11Arr);
        return mnemonicToString(words, chkWord);
    }

    /**
     * toKey converts a mnemonic generated using this library into the source
     * key used to create it. It returns an error if the passed mnemonic has an
     * incorrect checksum, if the number of words is unexpected, or if one
     * of the passed words is not found in the words list.
     * @param mnemonicStr words delimited by MNEMONIC_DELIM
     * @return 32 byte array key
     */
    public static byte[] toKey(String mnemonicStr) throws GeneralSecurityException {
        Objects.requireNonNull(mnemonicStr, "mnemonic must not be null");
        String[] mnemonic = mnemonicStr.split(MNEMONIC_DELIM);
        if (mnemonic.length != MNEM_LEN_WORDS) {
            throw new IllegalArgumentException("mnemonic does not have enough words");
        }
        // convert to uint11
        int numWords = MNEM_LEN_WORDS - CHECKSUM_LEN_WORDS;
        int[] uint11Arr = new int[numWords];
        for (int i = 0; i < numWords; i++) {
            uint11Arr[i] = -1;
        }
        for (int w = 0; w < Wordlist.RAW.length; w++) {
            for (int i = 0; i < numWords; i++) {
                if (Wordlist.RAW[w].equals(mnemonic[i])) {
                    uint11Arr[i] = w;
                }
            }
        }
        for (int i = 0; i < numWords; i++) {
            if (uint11Arr[i] == -1) {
                throw new IllegalArgumentException("mnemonic contains word that is not in word list");
            }
        }
        byte[] b = toByteArray(uint11Arr);
        // chop the last byte. The last byte was 3 bits, padded with 8 bits to create the 24th word.
        // Those last padded 8 bits is an extra zero byte.
        if (b.length != KEY_LEN_BYTES + 1) {
            throw new GeneralSecurityException("wrong key length");
        }
        if (b[KEY_LEN_BYTES] != (byte)0) {
            throw new GeneralSecurityException("unexpected byte from key");
        }
        byte[] bCopy = Arrays.copyOf(b, KEY_LEN_BYTES);
        String chkWord = checksum(bCopy);
        if (!chkWord.equals(mnemonic[MNEM_LEN_WORDS - CHECKSUM_LEN_WORDS])) {
            throw new GeneralSecurityException("checksum failed to validate");
        }
        return Arrays.copyOf(b, KEY_LEN_BYTES);
    }

    // returns a word corresponding to the 11 bit checksum of the data
    protected static String checksum(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance(CHECKSUM_ALG);
            digest.update(Arrays.copyOf(data, data.length));
            byte[] d = digest.digest();
            // optimize for CHECKSUM_LEN_WORDS = 1
            d = Arrays.copyOfRange(d, 0, 2);
            return applyWord(toUintNArray(d)[0]);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Assumes little-endian
    private static int[] toUintNArray(byte[] arr) {
        int buffer = 0;
        int numBits = 0;
        int[] out = new int[(arr.length*8+BITS_PER_WORD-1)/BITS_PER_WORD];

        int j = 0;

        for (int i = 0; i < arr.length; i++) {
            // numBits is how many bits in arr[i] we've processed
            int v = arr[i];
            if (v < 0) v += 256; // deal with java signed types
            buffer |= (v << numBits);
            numBits += 8;
            if (numBits >= BITS_PER_WORD) {
                // add to output
                out[j] = buffer&0x7ff;
                j++;
                // drop from buffer
                buffer = buffer >> BITS_PER_WORD;
                numBits -= BITS_PER_WORD;
            }
        }
        if (numBits != 0) {
            out[j] = buffer&0x7ff;
        }
        return out;
    }

    // reverses toUintNArray, might result in an extra byte
    // be careful since int is a signed type. But 11 < 32/2 so should be ok.
    private static byte[] toByteArray(int[] arr) {
        int buffer = 0;
        int numBits = 0;
        byte[] out = new byte[(arr.length*BITS_PER_WORD+8-1)/8];

        int j = 0;
        for (int i = 0; i < arr.length; i++) {
            buffer |= (arr[i] << numBits);
            numBits += BITS_PER_WORD;
            while (numBits >= 8) {
                out[j] = (byte)(buffer&0xff);
                j++;
                buffer = buffer >> 8;
                numBits -= 8;
            }
        }
        if (numBits != 0) {
            out[j] = (byte)(buffer&0xff);
        }
        return out;
    }

    private static String applyWord(int iN) {
        return Wordlist.RAW[iN];
    }

    private static String[] applyWords(int[] arrN) {
        String[] ret = new String[arrN.length];
        for (int i = 0; i < arrN.length; i++) {
            ret[i] = applyWord(arrN[i]);
        }
        return ret;
    }

    private static String mnemonicToString(String[] mnemonic, String checksum) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < mnemonic.length; i++) {
            if (i > 0) s.append(MNEMONIC_DELIM);
            s.append(mnemonic[i]);
        }
        s.append(MNEMONIC_DELIM);
        s.append(checksum);
        return s.toString();
    }
}
