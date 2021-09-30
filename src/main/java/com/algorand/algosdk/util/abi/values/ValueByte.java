package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.abi.types.TypeByte;

public class ValueByte extends Value {
    public ValueByte(byte b) {
        this.abiType = new TypeByte();
        this.value = b;
    }

    public byte[] encode() {
        return new byte[]{(byte) this.value};
    }
}
