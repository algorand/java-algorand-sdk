package com.algorand.algosdk.util.abi.types;

class StringT extends Type {
    StringT() {
    }

    @Override
    public String string() {
        return "string";
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof StringT;
    }

    @Override
    public int byteLen() throws IllegalAccessException {
        throw new IllegalArgumentException("Dynamic type cannot pre-compute byteLen");
    }
}