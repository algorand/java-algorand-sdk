package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.abi.types.TypeArrayStatic;

import java.util.Arrays;

public class ValueArrayStatic extends Value {
    public ValueArrayStatic(Value[] val) {
        if (val.length >= (1 << 16))
            throw new IllegalArgumentException("Cannot build static array: array length >= 2^16");
        if (val.length == 0)
            throw new IllegalArgumentException("Cannot build static array: array length 0");
        for (Value v : val) {
            if (!v.abiType.equals(val[0].abiType))
                throw new IllegalArgumentException("Cannot build static array: array element type not matching");
        }
        this.value = val;
        this.abiType = new TypeArrayStatic(val[0].abiType, val.length);
    }

    public byte[] encode() {
        return new ValueTuple((Value[]) this.value).encode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ValueArrayStatic))
            return false;
        if (!this.abiType.equals(((ValueArrayStatic) obj).abiType))
            return false;
        return Arrays.equals((Value[]) this.value, (Value[]) ((ValueArrayStatic) obj).value);
    }
}
