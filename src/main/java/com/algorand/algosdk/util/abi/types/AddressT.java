package com.algorand.algosdk.util.abi.types;

class AddressT extends Type {
    AddressT() {
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
        return obj instanceof AddressT;
    }

    @Override
    public int byteLen() throws IllegalAccessException {
        return 32;
    }
}