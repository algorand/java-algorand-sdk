package com.algorand.algosdk.util.abi.types;

import java.util.Objects;

class ArrayStaticT extends Type {
    public final Type elemType;
    public final int length;

    ArrayStaticT(Type elemType, int length) {
        if (length < 1)
            throw new IllegalArgumentException("static-array initialize failure: array length should be at least 1");
        this.elemType = elemType;
        this.length = length;
    }

    @Override
    public String string() throws IllegalAccessException {
        return this.elemType.string() + "[" + this.length + "]";
    }

    @Override
    public boolean isDynamic() throws IllegalAccessException {
        return this.elemType.isDynamic();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArrayStaticT)) return false;
        return length == ((ArrayStaticT) o).length && this.elemType.equals(((ArrayStaticT) o).elemType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elemType, length);
    }

    @Override
    public int byteLen() throws IllegalAccessException {
        return this.elemType.byteLen() * this.length;
    }
}