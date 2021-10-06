package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.abi.types.*;

import java.util.ArrayList;
import java.util.List;

public abstract class Value {
    public Type abiType;
    public Object value;

    Value() {
    }

    public abstract byte[] encode();

    public static Value decode(byte[] encoded, Type abiType) {
        if (abiType instanceof TypeUint)
            return new ValueUint((TypeUint) abiType, encoded);
        else if (abiType instanceof TypeUfixed)
            return new ValueUfixed((TypeUfixed) abiType, encoded);
        else if (abiType instanceof TypeAddress)
            return new ValueAddress(encoded);
        else if (abiType instanceof TypeBool)
            return new ValueBool(encoded);
        else if (abiType instanceof TypeByte)
            return new ValueByte(encoded);
        else if (abiType instanceof TypeString)
            return new ValueString(encoded);
        else if (abiType instanceof TypeArrayStatic)
            return new ValueArrayStatic((TypeArrayStatic) abiType, encoded);
        else if (abiType instanceof TypeArrayDynamic)
            return new ValueArrayDynamic(((TypeArrayDynamic) abiType).elemType, encoded);
        else if (abiType instanceof TypeTuple)
            return new ValueTuple((TypeTuple) abiType, encoded);
        throw new IllegalArgumentException("abiType cannot be inferred, decode failed");
    }

    public static byte[] getLengthEncoded(byte[] encoded) {
        if (encoded.length < 2)
            throw new IllegalArgumentException("encode byte size too small, less than 2 bytes");
        byte[] encodedLength = new byte[2];
        System.arraycopy(encoded, 0, encodedLength, 0, 2);
        return encodedLength;
    }

    public static byte[] getContentEncoded(byte[] encoded) {
        if (encoded.length < 2)
            throw new IllegalArgumentException("encode byte size too small, less than 2 bytes");
        byte[] encodedString = new byte[encoded.length - 2];
        System.arraycopy(encoded, 2, encodedString, 0, encoded.length - 2);
        return encodedString;
    }

    public static TypeTuple castToTupleType(int size, Type t) {
        List<Type> tupleTypes = new ArrayList<>();
        for (int i = 0; i < size; i++)
            tupleTypes.add(t);
        return new TypeTuple(tupleTypes);
    }
}
