package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.abi.types.Type;

public class ArrayDynamicV extends Value {
    public ArrayDynamicV(Value[] val, Type elemType) {
        if (val.length >= (1 << 16))
            throw new IllegalArgumentException("Cannot build dynamic array: array length >= 2^16");
        for (Value v : val) {
            if (!v.abiType.equals(elemType))
                throw new IllegalArgumentException("Cannot build dynamic array: array element type not matching");
        }
        this.value = val;
        this.abiType = elemType;
    }

    @Override
    public byte[] encode() throws IllegalAccessException {
        // TODO
        return  new byte[]{};
    }
}
