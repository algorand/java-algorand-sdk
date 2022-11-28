package com.algorand.algosdk.abi;

import com.algorand.algosdk.util.GenericObjToArray;

public class TypeArrayStatic extends ABIType {
    public final ABIType elemType;
    public final int length;

    public TypeArrayStatic(ABIType elemType, int length) {
        if (length < 0)
            throw new IllegalArgumentException("static-array initialize failure: array length should be positive");
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
        Object[] objArray = GenericObjToArray.unifyToArrayOfObjects(o);
        if (objArray.length != this.length)
            throw new IllegalArgumentException("cannot encode abi static array: length of value != length in array type");
        return ABIType.castToTupleType(this.length, this.elemType).encode(objArray);
    }

    @Override
    public Object decode(byte[] encoded) {
        return ABIType.castToTupleType(this.length, this.elemType).decode(encoded);
    }

    @Override
    public int byteLen() {
        if (this.elemType instanceof TypeBool)
            return (this.length + 7) / 8;
        return this.elemType.byteLen() * this.length;
    }
}
