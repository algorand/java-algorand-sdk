package com.algorand.algosdk.util.abi.types;

public class TypeAddress extends Type {
    public TypeAddress() {
    }

    @Override
    public String string() {
        return "address";
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TypeAddress;
    }

    @Override
    public int byteLen() throws IllegalAccessException {
        return 32;
    }
}