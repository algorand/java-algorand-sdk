package com.algorand.algosdk.util.abi.types;

class Address extends Type {
    Address() {
    }

    @Override
    public String string() {
        return "address";
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Address;
    }
}