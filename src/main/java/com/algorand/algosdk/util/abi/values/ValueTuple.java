package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.abi.types.TypeTuple;
import com.algorand.algosdk.util.abi.types.Type;

import java.util.Arrays;

public class ValueTuple extends Value {
    public ValueTuple(Value[] val) {
        if (val.length >= (1 << 16))
            throw new IllegalArgumentException("Cannot build tuple: element array length >= 2^16");
        Type[] elemTypes = new Type[val.length];
        for (int i = 0; i < val.length; i++)
            elemTypes[i] = val[i].abiType;
        this.abiType = new TypeTuple(Arrays.asList(elemTypes));
        this.value = val;
    }

    public byte[] encode() {
        // TODO
        return new byte[]{};
    }
}
