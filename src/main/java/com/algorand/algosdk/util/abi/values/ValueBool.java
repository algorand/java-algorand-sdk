package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.abi.types.TypeBool;

public class ValueBool extends Value {
    public ValueBool(boolean tf) {
        this.abiType = new TypeBool();
        this.value = tf;
    }

    public byte[] encode() {
        return ((boolean) this.value) ? new byte[]{(byte) 0x80} : new byte[]{0x00};
    }
}
