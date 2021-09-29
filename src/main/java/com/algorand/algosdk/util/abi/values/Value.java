package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.abi.types.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Value {
    public Type abiType;
    public Object value;

    Value() {
    }

    private Value(Value o) {
        this.abiType = o.abiType;
        this.value = o.value;
    }

    public byte[] encode() throws IllegalAccessException {
        throw new IllegalAccessException("Should not access to base value method: encode");
    }

    public Value(byte[] encoded, Type abiType) throws IllegalArgumentException {
        this(Value.decode(encoded, abiType));
    }

    public static byte[] encodeUintToBytes(BigInteger var, int byteNum) throws IllegalArgumentException {
        if (var.compareTo(BigInteger.ZERO) < 0)
            throw new IllegalArgumentException("Encode int to byte: input BigInteger < 0");
        if (var.compareTo(BigInteger.ONE.shiftLeft(byteNum * 8)) <= 0)
            throw new IllegalArgumentException("Encode int to byte: integer size exceeds the given byte number");

        byte[] buffer = new byte[byteNum];
        byte[] encoded = var.toByteArray();
        // in case number >= 2^(byteNum * 8 - 1), the 2's complement representation will extend 1 byte header of 0's
        if (encoded.length == byteNum + 1)
            encoded = Arrays.copyOfRange(encoded, 1, encoded.length);

        for (int i = 0; i < encoded.length; i++)
            buffer[buffer.length - 1 - i] = encoded[encoded.length - 1 - i];

        return buffer;
    }

    public static BigInteger decodeBytesToUint(byte[] encoded) {
        BigInteger res = BigInteger.ZERO;
        for (byte b : encoded) {
            res = res.shiftLeft(8);
            res = res.add(BigInteger.valueOf(b));
        }
        return res;
    }

    public static Value decode(byte[] encoded, Type abiType) {
        if (abiType instanceof UintT) {
            if (encoded.length != ((UintT) abiType).bitSize / 8)
                throw new IllegalArgumentException("encoded byte size not match with uint byte size");
            return new UintV(((UintT) abiType).bitSize, Value.decodeBytesToUint(encoded));
        } else if (abiType instanceof UfixedT) {
            if (encoded.length != ((UfixedT) abiType).bitSize / 8)
                throw new IllegalArgumentException("encoded byte size not match with ufixed byte size");
            return new UfixedV((UfixedT) abiType, Value.decodeBytesToUint(encoded));
        } else if (abiType instanceof AddressT) {
            if (encoded.length != 32)
                throw new IllegalArgumentException("encode byte size not match with address byte size");
            return new AddressV(encoded);
        } else if (abiType instanceof BoolT) {
            if (encoded.length != 1)
                throw new IllegalArgumentException("encode byte size not match with bool byte size");
            if (encoded[0] == 0x00)
                return new BoolV(false);
            else if (encoded[0] == (byte) 0x80)
                return new BoolV(true);
            else
                throw new IllegalArgumentException("encoded byte for bool is not either 0x80 or 0x00");
        } else if (abiType instanceof ByteT) {
            if (encoded.length != 1)
                throw new IllegalArgumentException("encode byte size not match with byteT byte size");
            return new ByteV(encoded[0]);
        } else if (abiType instanceof StringT) {
            if (encoded.length < 2)
                throw new IllegalArgumentException("encode byte size too small, less than 2 bytes");
            byte[] encodedLength = new byte[2];
            byte[] encodedString = new byte[encoded.length - 2];
            System.arraycopy(encoded, 0, encodedLength, 0, 2);
            System.arraycopy(encoded, 2, encodedString, 0, encoded.length - 2);
            if (!Value.decodeBytesToUint(encodedLength).equals(BigInteger.valueOf(encodedString.length)))
                throw new IllegalArgumentException("string decode failure: encoded bytes do not match with length header");
            return new StringV(new String(encodedString, StandardCharsets.UTF_8));
        } else if (abiType instanceof ArrayStaticT) {

        } else if (abiType instanceof ArrayDynamicT) {

        } else if (abiType instanceof TupleT) {

        }
        throw new IllegalArgumentException("abiType cannot be inferred, decode failed");
    }
}
