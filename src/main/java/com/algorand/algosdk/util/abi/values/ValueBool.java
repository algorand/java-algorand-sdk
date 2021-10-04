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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ValueBool))
            return false;
        if (!this.abiType.equals(((ValueBool) obj).abiType))
            return false;
        return this.value == ((ValueBool) obj).value;
    }
}
