package com.algorand.algosdk.util.abi.types;

class Bool extends Type {
    Bool() {
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
        return obj instanceof Bool;
    }
}