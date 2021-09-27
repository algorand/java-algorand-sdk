package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.abi.types.AddressT;

public class AddressV extends Value {
    public AddressV(byte[] address) {
        this.abiType = new AddressT();
        if (address.length != 32)
            throw new IllegalArgumentException("cannot construct ABI address, address byte length 32");
        this.value = address;
    }

    @Override
    public byte[] encode() throws IllegalAccessException {
        return (byte[]) this.value;
    }
}
