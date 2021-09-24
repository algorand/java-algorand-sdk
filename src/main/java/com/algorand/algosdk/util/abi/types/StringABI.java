package com.algorand.algosdk.util.abi.types;

class StringABI extends Type {
    StringABI() {}

    @Override
    public String string() { return "string"; }

    @Override
    public boolean isDynamic() { return true; }

    @Override
    public boolean equals(Object obj) { return obj instanceof StringABI; }
}