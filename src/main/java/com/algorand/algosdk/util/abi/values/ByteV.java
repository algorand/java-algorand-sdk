package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.abi.types.ByteT;

public class ByteV extends Value {
    public ByteV(byte b) {
        this.abiType = new ByteT();
        this.value = b;
    }

    @Override
    public byte[] encode() throws IllegalAccessException {
        return new byte[]{(byte) this.value};
    }
}
