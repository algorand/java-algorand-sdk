package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.abi.types.Type;

public class Value {
    public Type abiType;
    public Object value;

    Value(Type abiType, Object value) {
        this.abiType = abiType;
        this.value = value;
    }

    public byte[] encode() throws IllegalAccessException {
        throw new IllegalAccessException("Should not access to base value method: encode");
    }

    Value(byte[] encoded) throws IllegalArgumentException {
        throw new IllegalArgumentException("Should not access to base value constructor: decode");
    }

    public static Value decode(byte[] encoded, Type abiType) {
        // TODO implementation
        return new Value(new Type(), new Object());
    }
}
