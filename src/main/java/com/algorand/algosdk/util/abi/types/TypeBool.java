package com.algorand.algosdk.util.abi.types;

public class TypeBool extends Type {
    public TypeBool() {
    }

    @Override
    public String string() {
        return "bool";
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TypeBool;
    }

    @Override
    public int byteLen() throws IllegalAccessException {
        return 1;
    }
}