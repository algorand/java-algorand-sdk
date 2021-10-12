package com.algorand.algosdk.util.abi;

public class TypeByte extends Type {
    public TypeByte() {
    }

    @Override
    public String toString() {
        return "byte";
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TypeByte;
    }

    @Override
    public byte[] encode(Object o) {
        if (!(o instanceof Byte))
            throw new IllegalArgumentException("cannot infer type for byte abi value encode");
        return new byte[]{(byte) o};
    }

    @Override
    public Object decode(byte[] encoded) {
        if (encoded.length != 1)
            throw new IllegalArgumentException("cannot decode abi byte value, byte length not matching");
        return encoded[0];
    }

    @Override
    public int byteLen() {
        return 1;
    }
}
