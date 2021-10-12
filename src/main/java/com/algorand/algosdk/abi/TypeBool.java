package com.algorand.algosdk.abi;

public class TypeBool extends Type {
    public TypeBool() {
    }

    @Override
    public String toString() {
        return "bool";
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TypeBool;
    }

    @Override
    public byte[] encode(Object o) {
        if (!(o instanceof Boolean))
            throw new IllegalArgumentException("cannot infer type for boolean abi value encode");
        boolean res = (boolean) o;
        return new byte[]{res ? (byte) 0x80 : 0x00};
    }

    @Override
    public Object decode(byte[] encoded) {
        if (encoded.length != 1)
            throw new IllegalArgumentException("cannot decode abi bool value, byte length do not match");
        if (encoded[0] == (byte) 0x80)
            return true;
        else if (encoded[0] == 0x00)
            return false;
        throw new IllegalArgumentException("cannot decode abi bool value, illegal encoding value");
    }

    @Override
    public int byteLen() {
        return 1;
    }
}
