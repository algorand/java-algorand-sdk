package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.abi.types.Type;
import com.algorand.algosdk.util.abi.types.UfixedT;
import com.algorand.algosdk.util.abi.types.UintT;

import java.math.BigInteger;

public class UintV extends Value {
    public UintV(int size, BigInteger value) {
        Type uintT = new UintT(size);
        BigInteger upperLimit = BigInteger.ONE.shiftLeft(size);
        if (value.compareTo(upperLimit) >= 0)
            throw new IllegalArgumentException("cannot construct ABI uint value: passed in value larger than Uint size");
        this.abiType = uintT;
        this.value = value;
    }

    @Override
    public byte[] encode() throws IllegalAccessException {
        int uintSize = ((UfixedT) this.abiType).bitSize;
        return Value.encodeUintToBytes(((BigInteger) this.value), uintSize);
    }
}
