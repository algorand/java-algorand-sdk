package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.abi.types.Type;
import com.algorand.algosdk.util.abi.types.TypeUfixed;
import com.algorand.algosdk.util.abi.types.TypeUint;

import java.math.BigInteger;

public class ValueUint extends Value {
    public ValueUint(int size, BigInteger value) {
        Type uintT = new TypeUint(size);
        BigInteger upperLimit = BigInteger.ONE.shiftLeft(size);
        if (value.compareTo(upperLimit) >= 0)
            throw new IllegalArgumentException("cannot construct ABI uint value: passed in value larger than Uint size");
        this.abiType = uintT;
        this.value = value;
    }

    @Override
    public byte[] encode() throws IllegalAccessException {
        int uintSize = ((TypeUfixed) this.abiType).bitSize;
        return Value.encodeUintToBytes(((BigInteger) this.value), uintSize);
    }
}
