package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.util.abi.types.Type;
import com.algorand.algosdk.util.abi.types.TypeUint;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class ValueUint extends Value {
    public ValueUint(int size, BigInteger value) {
        Type uintT = new TypeUint(size);
        BigInteger upperLimit = BigInteger.ONE.shiftLeft(size);
        if (value.compareTo(upperLimit) >= 0)
            throw new IllegalArgumentException("cannot construct ABI uint value: passed in value larger than Uint size");
        this.abiType = uintT;
        this.value = value;
    }

    public ValueUint(byte val) {
        this.abiType = new TypeUint(8);
        this.value = new BigInteger(1, new byte[]{val});
    }

    public ValueUint(short val) {
        this.abiType = new TypeUint(16);
        this.value = new BigInteger(1, ByteBuffer.allocate(Short.SIZE / Byte.SIZE).putShort(val).array());
    }

    public ValueUint(int val) {
        this.abiType = new TypeUint(32);
        this.value = new BigInteger(1, ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(val).array());
    }

    public ValueUint(long val) {
        this.abiType = new TypeUint(64);
        this.value = new BigInteger(1, ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(val).array());
    }

    public byte[] encode() {
        int uintSize = ((TypeUint) this.abiType).bitSize;
        return Encoder.encodeUintToBytes(((BigInteger) this.value), uintSize / Byte.SIZE);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ValueUint))
            return false;
        if (!((ValueUint) obj).abiType.equals(this.abiType))
            return false;
        BigInteger thisVal = (BigInteger) this.value;
        BigInteger otherVal = (BigInteger) ((ValueUint) obj).value;
        return thisVal.equals(otherVal);
    }
}
