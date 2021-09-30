package com.algorand.algosdk.util.abi.types;

public class TypeAddress extends Type {
    public TypeAddress() {
    }

    @Override
    public String toString() {
        return "address";
    }

    public boolean isDynamic() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TypeAddress;
    }

    public int byteLen() {
        return 32;
    }
}