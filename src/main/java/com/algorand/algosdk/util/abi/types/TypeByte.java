package com.algorand.algosdk.util.abi.types;

public class TypeByte extends Type {
    public TypeByte() {
    }

    @Override
    public String toString() {
        return "byte";
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TypeByte;
    }

    @Override
    public int byteLen() {
        return 1;
    }
}
