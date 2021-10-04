package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.abi.types.TypeAddress;

import java.util.Arrays;

public class ValueAddress extends Value {
    public ValueAddress(byte[] address) {
        this.abiType = new TypeAddress();
        if (address.length != 32)
            throw new IllegalArgumentException("cannot construct ABI address, address byte length 32");
        this.value = address;
    }

    public byte[] encode() {
        Value[] values = new Value[32];
        for (int i = 0; i < 32; i++)
            values[i] = new ValueByte(((byte[]) this.value)[i]);
        Value casted = new ValueTuple(values);
        return casted.encode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ValueAddress))
            return false;
        if (!this.abiType.equals(((ValueAddress) obj).abiType))
            return false;
        return Arrays.equals(
                (byte[]) this.value,
                (byte[]) ((ValueAddress) obj).value
        );
    }
}
