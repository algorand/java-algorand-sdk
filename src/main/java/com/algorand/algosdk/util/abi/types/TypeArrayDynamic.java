package com.algorand.algosdk.util.abi.types;

import java.util.Objects;

public class TypeArrayDynamic extends Type {
    public final Type elemType;

    public TypeArrayDynamic(Type elemType) {
        this.elemType = elemType;
    }

    @Override
    public String toString() {
        return this.elemType.toString() + "[]";
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TypeArrayDynamic)) return false;
        return this.elemType.equals(((TypeArrayDynamic) o).elemType);
    }

    @Override
    public int byteLen() {
        throw new IllegalArgumentException("Dynamic type cannot pre-compute byteLen");
    }
}
