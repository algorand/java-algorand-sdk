package com.algorand.algosdk.util.abi.types;

public class TypeBool extends Type {
    public TypeBool() {
    }

    @Override
    public String toString() {
        return "bool";
    }

    public boolean isDynamic() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TypeBool;
    }

    public int byteLen() {
        return 1;
    }
}
