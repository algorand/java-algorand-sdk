package com.algorand.algosdk.util.abi.types;

import java.util.Objects;

public class TypeArrayStatic extends Type {
    public final Type elemType;
    public final int length;

    public TypeArrayStatic(Type elemType, int length) {
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
        if (!(o instanceof TypeArrayStatic)) return false;
        return length == ((TypeArrayStatic) o).length && this.elemType.equals(((TypeArrayStatic) o).elemType);
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