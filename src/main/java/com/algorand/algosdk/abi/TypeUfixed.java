package com.algorand.algosdk.abi;

import com.algorand.algosdk.util.Encoder;

import java.math.BigInteger;

public class TypeUfixed extends Type {
    public final int bitSize;
    public final int precision;

    public TypeUfixed(int size, int precision) {
        if (size < 8 || size > 512 || size % 8 != 0)
            throw new IllegalArgumentException(
                    "ufixed initialize failure: bitSize should be in [8, 512] and bitSize mod 8 == 0"
            );
        if (precision < 1 || precision > 160)
            throw new IllegalArgumentException("ufixed initialize failure: precision should be in [1, 160]");
        this.bitSize = size;
        this.precision = precision;
    }

    @Override
    public String toString() {
        return "ufixed" + this.bitSize + "x" + this.precision;
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TypeUfixed)) return false;
        return bitSize == ((TypeUfixed) o).bitSize && precision == ((TypeUfixed) o).precision;
    }

    @Override
    public byte[] encode(Object obj) {
        BigInteger value;
        if (obj instanceof BigInteger)
            value = (BigInteger) obj;
        else if (obj instanceof Short)
            value = BigInteger.valueOf((short) obj);
        else if (obj instanceof Integer)
            value = BigInteger.valueOf((int) obj);
        else if (obj instanceof Long)
            value = BigInteger.valueOf((long) obj);
        else if (obj instanceof Byte)
            value = BigInteger.valueOf((byte) obj);
        else
            throw new IllegalArgumentException("cannot infer type for ufixed value encode");
        return Encoder.encodeUintToBytes(value, this.bitSize / Byte.SIZE);
    }

    @Override
    public Object decode(byte[] encoded) {
        if (encoded.length != bitSize / Byte.SIZE)
            throw new IllegalArgumentException("cannot decode for abi ufixed value, byte length not matching");
        return Encoder.decodeBytesToUint(encoded);
    }

    @Override
    public int byteLen() {
        return this.bitSize / 8;
    }
}
