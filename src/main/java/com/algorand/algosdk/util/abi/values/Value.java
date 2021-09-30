package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.util.abi.types.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public abstract class Value {
    public Type abiType;
    public Object value;

    Value() {
    }

    private Value(Value o) {
        this.abiType = o.abiType;
        this.value = o.value;
    }

    public abstract byte[] encode();

    public Value(byte[] encoded, Type abiType) {
        this(Value.decode(encoded, abiType));
    }

    public static Value decode(byte[] encoded, Type abiType) {
        if (abiType instanceof TypeUint) {
            if (encoded.length != ((TypeUint) abiType).bitSize / 8)
                throw new IllegalArgumentException("encoded byte size not match with uint byte size");
            return new ValueUint(((TypeUint) abiType).bitSize, Encoder.decodeBytesToUint(encoded));
        } else if (abiType instanceof TypeUfixed) {
            if (encoded.length != ((TypeUfixed) abiType).bitSize / 8)
                throw new IllegalArgumentException("encoded byte size not match with ufixed byte size");
            return new ValueUfixed((TypeUfixed) abiType, Encoder.decodeBytesToUint(encoded));
        } else if (abiType instanceof TypeAddress) {
            if (encoded.length != 32)
                throw new IllegalArgumentException("encode byte size not match with address byte size");
            return new ValueAddress(encoded);
        } else if (abiType instanceof TypeBool) {
            if (encoded.length != 1)
                throw new IllegalArgumentException("encode byte size not match with bool byte size");
            if (encoded[0] == 0x00)
                return new ValueBool(false);
            else if (encoded[0] == (byte) 0x80)
                return new ValueBool(true);
            else
                throw new IllegalArgumentException("encoded byte for bool is not either 0x80 or 0x00");
        } else if (abiType instanceof TypeByte) {
            if (encoded.length != 1)
                throw new IllegalArgumentException("encode byte size not match with byteT byte size");
            return new ValueByte(encoded[0]);
        } else if (abiType instanceof TypeString) {
            if (encoded.length < 2)
                throw new IllegalArgumentException("encode byte size too small, less than 2 bytes");
            byte[] encodedLength = new byte[2];
            byte[] encodedString = new byte[encoded.length - 2];
            System.arraycopy(encoded, 0, encodedLength, 0, 2);
            System.arraycopy(encoded, 2, encodedString, 0, encoded.length - 2);
            if (!Encoder.decodeBytesToUint(encodedLength).equals(BigInteger.valueOf(encodedString.length)))
                throw new IllegalArgumentException("string decode failure: encoded bytes do not match with length header");
            return new ValueString(new String(encodedString, StandardCharsets.UTF_8));
        } else if (abiType instanceof TypeArrayStatic) {

        } else if (abiType instanceof TypeArrayDynamic) {

        } else if (abiType instanceof TypeTuple) {

        }
        throw new IllegalArgumentException("abiType cannot be inferred, decode failed");
    }
}
