package com.algorand.algosdk.util.abi.types;

class Byte extends Type {
    Byte() {}

    @Override
    public String string() { return "byte"; }

    @Override
    public boolean isDynamic() { return false; }

    @Override
    public boolean equals(Object obj) { return obj instanceof Byte; }
}
