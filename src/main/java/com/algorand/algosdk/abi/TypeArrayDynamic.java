package com.algorand.algosdk.abi;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.util.GenericObjToArray;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class TypeArrayDynamic extends ABIType {
    public final ABIType elemType;

    public TypeArrayDynamic(ABIType elemType) {
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
        Object[] objArray = GenericObjToArray.unifyToArrayOfObjects(o);

        byte[] castedEncode = ABIType.castToTupleType(objArray.length, this.elemType).encode(objArray);
        byte[] lengthEncode = Encoder.encodeUintToBytes(BigInteger.valueOf(objArray.length), ABI_DYNAMIC_HEAD_BYTE_LEN);

        ByteBuffer bf = ByteBuffer.allocate(castedEncode.length + ABI_DYNAMIC_HEAD_BYTE_LEN);
        bf.put(lengthEncode);
        bf.put(castedEncode);
        return bf.array();
    }

    @Override
    public Object decode(byte[] encoded) {
        byte[] encodedLength = ABIType.getLengthEncoded(encoded);
        byte[] encodedArray = ABIType.getContentEncoded(encoded);
        return ABIType.castToTupleType(Encoder.decodeBytesToUint(encodedLength).intValue(), this.elemType).decode(encodedArray);
    }

    @Override
    public int byteLen() {
        throw new IllegalArgumentException("Dynamic type cannot pre-compute byteLen");
    }
}
