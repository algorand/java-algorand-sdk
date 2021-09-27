package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.abi.types.BoolT;

public class BoolV extends Value {
    public BoolV(boolean tf) {
        this.abiType = new BoolT();
        this.value = tf;
    }

    @Override
    public byte[] encode() throws IllegalAccessException {
        if ((boolean) this.value)
            return new byte[]{(byte) 0x80};
        else
            return new byte[]{0x00};
    }
}
