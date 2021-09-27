package com.algorand.algosdk.util.abi.types;

class BoolT extends Type {
    BoolT() {
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
        return obj instanceof BoolT;
    }

    @Override
    public int byteLen() throws IllegalAccessException {
        return 1;
    }
}