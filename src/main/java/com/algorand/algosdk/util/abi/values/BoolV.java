package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.abi.types.BoolT;

public class BoolV extends Value {
    public BoolV(boolean tf) {
        this.abiType = new BoolT();
        this.value = tf;
    }

    @Override
    public byte[] encode() throws IllegalAccessException {
        return ((boolean) this.value) ? new byte[]{(byte) 0x80} : new byte[]{0x00};
    }
}
