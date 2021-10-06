package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.abi.types.TypeByte;

public class ValueByte extends Value {
    public ValueByte(byte b) {
        this.abiType = new TypeByte();
        this.value = b;
    }

    public ValueByte(byte[] encoded) {
        if (encoded.length != 1)
            throw new IllegalArgumentException("encode byte size not match with byteT byte size");
        this.abiType = new TypeByte();
        this.value = encoded[0];
    }

    public byte[] encode() {
        return new byte[]{(byte) this.value};
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ValueByte))
            return false;
        if (!this.abiType.equals(((ValueByte) obj).abiType))
            return false;
        return this.value.equals(((ValueByte) obj).value);
    }
}
