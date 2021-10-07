package com.algorand.algosdk.util.abi;

import com.algorand.algosdk.util.Encoder;

import java.math.BigInteger;
import java.nio.ByteBuffer;

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
    public byte[] encode(Object o) {
        if (!(o instanceof Object[]))
            throw new IllegalArgumentException("cannot infer type for abi dynamic array encode");
        Object[] objArray = (Object[]) o;

        byte[] castedEncode = Type.castToTupleType(objArray.length, this.elemType).encode(objArray);
        byte[] lengthEncode = Encoder.encodeUintToBytes(BigInteger.valueOf(objArray.length), 2);

        ByteBuffer bf = ByteBuffer.allocate(castedEncode.length + 2);
        bf.put(lengthEncode);
        bf.put(castedEncode);
        return bf.array();
    }

    @Override
    public Object decode(byte[] encoded) {
        byte[] encodedLength = Type.getLengthEncoded(encoded);
        byte[] encodedArray = Type.getContentEncoded(encoded);
        return Type.castToTupleType(Encoder.decodeBytesToUint(encodedLength).intValue(), this.elemType).decode(encodedArray);
    }

    @Override
    public int byteLen() {
        throw new IllegalArgumentException("Dynamic type cannot pre-compute byteLen");
    }
}
