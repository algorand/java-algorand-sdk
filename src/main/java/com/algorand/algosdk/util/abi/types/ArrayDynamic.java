package com.algorand.algosdk.util.abi.types;

import java.util.Objects;

class ArrayDynamic extends Type {
    public final Type elemType;

    ArrayDynamic(Type elemType) {
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
        if (!(o instanceof ArrayDynamic)) return false;
        return this.elemType.equals(((ArrayDynamic) o).elemType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elemType);
    }
}
