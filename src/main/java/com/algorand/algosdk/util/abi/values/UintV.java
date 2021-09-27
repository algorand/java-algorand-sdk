package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.abi.types.Type;
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
        int uintSize = ((UintT) this.abiType).bitSize;
        byte[] buffer = new byte[uintSize];
        byte[] castToBytes = ((BigInteger) this.value).toByteArray();
        System.arraycopy(castToBytes, 0, buffer, buffer.length - castToBytes.length, castToBytes.length);
        return buffer;
    }
}
