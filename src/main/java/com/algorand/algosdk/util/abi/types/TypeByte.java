package com.algorand.algosdk.util.abi.types;

public class TypeByte extends Type {
    public TypeByte() {
    }

    @Override
    public String toString() {
        return "byte";
    }

    public boolean isDynamic() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TypeByte;
    }

    public int byteLen() {
        return 1;
    }
}
