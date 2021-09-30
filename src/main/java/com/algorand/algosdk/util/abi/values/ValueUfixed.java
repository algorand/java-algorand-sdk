package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.abi.types.TypeUfixed;

import java.math.BigInteger;

public class ValueUfixed extends Value {
    public ValueUfixed(TypeUfixed ufixedT, BigInteger value) {
        BigInteger upperLimit = BigInteger.ONE.shiftLeft(ufixedT.bitSize);
        if (value.compareTo(upperLimit) >= 0)
            throw new IllegalArgumentException("cannot construct ABI ufixed value: passed in value larger than ufixed size");
        this.abiType = ufixedT;
        this.value = value;
    }

    public ValueUfixed(int size, int precision, BigInteger value) {
        this(new TypeUfixed(size, precision), value);
    }

    @Override
    public byte[] encode() throws IllegalAccessException {
        int ufixedSize = ((TypeUfixed) this.abiType).bitSize;
        return Value.encodeUintToBytes(((BigInteger) this.value), ufixedSize);
    }
}
