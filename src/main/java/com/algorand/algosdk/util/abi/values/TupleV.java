package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.abi.types.TupleT;
import com.algorand.algosdk.util.abi.types.Type;

import java.util.Arrays;

public class TupleV extends Value {
    public TupleV(Value[] val) {
        if (val.length >= (1 << 16))
            throw new IllegalArgumentException("Cannot build tuple: element array length >= 2^16");
        Type[] elemTypes = new Type[val.length];
        for (int i = 0; i < val.length; i++)
            elemTypes[i] = val[i].abiType;
        this.abiType = new TupleT(Arrays.asList(elemTypes));
        this.value = val;
    }

    @Override
    public byte[] encode() throws IllegalAccessException {
        // TODO
        return new byte[]{};
    }
}
