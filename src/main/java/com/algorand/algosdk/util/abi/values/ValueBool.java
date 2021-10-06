package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.abi.types.TypeBool;

public class ValueBool extends Value {
    public ValueBool(boolean tf) {
        this.abiType = new TypeBool();
        this.value = tf;
    }

    public ValueBool(byte[] encoded) {
        if (encoded.length != 1)
            throw new IllegalArgumentException("encode byte size not match with bool byte size");
        if (encoded[0] == 0x00)
            this.value = false;
        else if (encoded[0] == (byte) 0x80)
            this.value = true;
        else
            throw new IllegalArgumentException("encoded byte for bool is not either 0x80 or 0x00");
        this.abiType = new TypeBool();
    }

    public byte[] encode() {
        return ((boolean) this.value) ? new byte[]{(byte) 0x80} : new byte[]{0x00};
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ValueBool))
            return false;
        if (!this.abiType.equals(((ValueBool) obj).abiType))
            return false;
        return this.value == ((ValueBool) obj).value;
    }
}
