package com.algorand.algosdk.abi;

import com.algorand.algosdk.util.Encoder;

import java.math.BigInteger;

public class TypeUint extends Type {
    public final int bitSize;

    public TypeUint(int size) {
        if (size < 8 || size > 512 || size % 8 != 0)
            throw new IllegalArgumentException(
                    "uint initialize failure: bitSize should be in [8, 512] and bitSize mod 8 == 0"
            );
        this.bitSize = size;
    }

    @Override
    public String toString() {
        return "uint" + this.bitSize;
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TypeUint)) return false;
        return this.bitSize == ((TypeUint) o).bitSize;
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
            throw new IllegalArgumentException("cannot infer type for uint value encode");
        return Encoder.encodeUintToBytes(value, this.bitSize / Byte.SIZE);
    }

    @Override
    public Object decode(byte[] encoded) {
        if (encoded.length != bitSize / Byte.SIZE)
            throw new IllegalArgumentException("cannot decode for abi uint value, byte length not matching");
        return Encoder.decodeBytesToUint(encoded);
    }

    @Override
    public int byteLen() {
        return this.bitSize / 8;
    }
}
