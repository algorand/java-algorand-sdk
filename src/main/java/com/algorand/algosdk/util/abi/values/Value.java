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
