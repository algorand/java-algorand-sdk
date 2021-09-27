package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.abi.types.UfixedT;

import java.math.BigInteger;

public class UfixedV extends Value {
    public UfixedV(UfixedT ufixedT, BigInteger value) {
        BigInteger upperLimit = BigInteger.ONE.shiftLeft(ufixedT.bitSize);
        if (value.compareTo(upperLimit) >= 0)
            throw new IllegalArgumentException("cannot construct ABI ufixed value: passed in value larger than ufixed size");
        this.abiType = ufixedT;
        this.value = value;
    }

    public UfixedV(int size, int precision, BigInteger value) {
        this(new UfixedT(size, precision), value);
    }

    @Override
    public byte[] encode() throws IllegalAccessException {
        int ufixedSize = ((UfixedT) this.abiType).bitSize;
        byte[] buffer = new byte[ufixedSize];
        byte[] castToBytes = ((BigInteger) this.value).toByteArray();
        System.arraycopy(castToBytes, 0, buffer, buffer.length - castToBytes.length, castToBytes.length);
        return buffer;
    }
}
