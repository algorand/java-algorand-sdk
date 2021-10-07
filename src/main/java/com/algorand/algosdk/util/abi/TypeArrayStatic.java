package com.algorand.algosdk.util.abi;

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

    @Override
    public boolean isDynamic() {
        return this.elemType.isDynamic();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TypeArrayStatic)) return false;
        return length == ((TypeArrayStatic) o).length && this.elemType.equals(((TypeArrayStatic) o).elemType);
    }

    @Override
    public byte[] encode(Object o) {
        if (!o.getClass().isArray())
            throw new IllegalArgumentException("cannot infer type for abi static array type encode");
        Object[] objArray = (Object[]) o;
        if (objArray.length != this.length)
            throw new IllegalArgumentException("cannot encode abi static array: length of value != length in array type");
        return Type.castToTupleType(this.length, this.elemType).encode(objArray);
    }

    @Override
    public Object decode(byte[] encoded) {
        return Type.castToTupleType(this.length, this.elemType).decode(encoded);
    }

    @Override
    public int byteLen() {
        if (this.elemType instanceof TypeBool)
            return (this.length + 7) / 8;
        return this.elemType.byteLen() * this.length;
    }
}
