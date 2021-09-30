package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.abi.types.TypeAddress;

public class ValueAddress extends Value {
    public ValueAddress(byte[] address) {
        this.abiType = new TypeAddress();
        if (address.length != 32)
            throw new IllegalArgumentException("cannot construct ABI address, address byte length 32");
        this.value = address;
    }

    @Override
    public byte[] encode() throws IllegalAccessException {
        return (byte[]) this.value;
    }
}
