package com.algorand.algosdk.util.abi.types;

public class TypeString extends Type {
    public TypeString() {
    }

    @Override
    public String toString() {
        return "string";
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TypeString;
    }

    @Override
    public int byteLen() {
        throw new IllegalArgumentException("Dynamic type cannot pre-compute byteLen");
    }
}
