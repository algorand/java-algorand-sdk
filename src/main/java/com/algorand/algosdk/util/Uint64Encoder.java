package com.algorand.algosdk.util;

import java.math.BigInteger;

public class Uint64Encoder {

    /**
     * The length of an encoded uint64, in bytes.
     */
    public static final int UINT64_LENGTH = 8;

    /**
     * The maximum value that a uint64 can contain.
     */
    public static final BigInteger MAX_UINT64 = new BigInteger("FFFFFFFFFFFFFFFF", 16);
    
    /**
     * Encode an non-negative integer as a big-endian uint64.
     * @param value The value to encode.
     * @throws IllegalArgumentException if value is negative.
     * @return A byte array containing the big-endian encoding of the value. Its length will be Uint64Encoder.UINT64_LENGTH.
     */
    public static byte[] encode(long value) {
        return Uint64Encoder.encode(BigInteger.valueOf(value));
    }

    /**
     * Encode an non-negative integer as a big-endian uint64.
     * @param value The value to encode.
     * @throws IllegalArgumentException if value cannot be represented by a uint64.
     * @return A byte array containing the big-endian encoding of the value. Its length will be Uint64Encoder.UINT64_LENGTH.
     */
    public static byte[] encode(BigInteger value) {
        if (value.compareTo(BigInteger.ZERO) < 0 || value.compareTo(MAX_UINT64) > 0) {
            throw new IllegalArgumentException("Value cannot be represented by a uint64");
        }

        byte[] fixedLengthEncoding = new byte[Uint64Encoder.UINT64_LENGTH];

        byte[] encodedValue = value.toByteArray();
        int offset = 0;
        if (encodedValue.length > 0 && encodedValue[0] == 0) {
            // encodedValue is actually encoded as a signed 2's complement value, so there may be a
            // leading 0 for some encodings -- ignore it
            offset = 1;
        }

        System.arraycopy(encodedValue, offset, fixedLengthEncoding, Uint64Encoder.UINT64_LENGTH - encodedValue.length + offset, encodedValue.length - offset);

        return fixedLengthEncoding;
    }

    /**
     * Decode an encoded big-endian uint64 value.
     * @param encoded The encoded uint64 value. Its length must be Uint64Encoder.UINT64_LENGTH.
     * @throws IllegalArgumentException if encoded is the wrong length.
     * @return The decoded value.
     */
    public static BigInteger decode(byte[] encoded) {
        if (encoded.length != Uint64Encoder.UINT64_LENGTH) {
            throw new IllegalArgumentException("Length of byte array is invalid");
        }

        return new BigInteger(1, encoded);
    }

}
