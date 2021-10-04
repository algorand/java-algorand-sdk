package com.algorand.algosdk.util.abi.types;

import java.util.ArrayList;
import java.util.List;
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
    public String toString() {
        return this.elemType.toString() + "[" + this.length + "]";
    }

    public boolean isDynamic() {
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

    public int byteLen() {
        if (this.elemType instanceof TypeBool) {
            List<Type> castedTupleElems = new ArrayList<>();
            for (int i = 0; i < this.length; i++)
                castedTupleElems.add(new TypeBool());
            TypeTuple casted = new TypeTuple(castedTupleElems);
            return casted.byteLen();
        }
        return this.elemType.byteLen() * this.length;
    }
}