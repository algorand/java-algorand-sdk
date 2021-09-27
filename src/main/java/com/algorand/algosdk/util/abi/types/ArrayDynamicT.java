package com.algorand.algosdk.util.abi.types;

import java.util.Objects;

class ArrayDynamicT extends Type {
    public final Type elemType;

    ArrayDynamicT(Type elemType) {
        this.elemType = elemType;
    }

    @Override
    public String string() throws IllegalAccessException {
        return this.elemType.string() + "[]";
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArrayDynamicT)) return false;
        return this.elemType.equals(((ArrayDynamicT) o).elemType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elemType);
    }

    @Override
    public int byteLen() throws IllegalAccessException {
        throw new IllegalArgumentException("Dynamic type cannot pre-compute byteLen");
    }
}
