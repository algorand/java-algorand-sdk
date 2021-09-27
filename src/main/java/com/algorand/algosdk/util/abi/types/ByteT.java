package com.algorand.algosdk.util.abi.types;

public class ByteT extends Type {
    public ByteT() {
    }

    @Override
    public String string() {
        return "byte";
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ByteT;
    }

    @Override
    public int byteLen() throws IllegalAccessException {
        return 1;
    }
}
