package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.util.abi.types.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class Value {
    public Type abiType;
    public Object value;

    Value() {
    }

    private Value(Value o) {
        this.abiType = o.abiType;
        this.value = o.value;
    }

    public abstract byte[] encode();

    public Value(byte[] encoded, Type abiType) {
        this(Value.decode(encoded, abiType));
    }

    public static Value decode(byte[] encoded, Type abiType) {
        if (abiType instanceof TypeUint) {
            if (encoded.length != ((TypeUint) abiType).bitSize / 8)
                throw new IllegalArgumentException("encoded byte size not match with uint byte size");
            return new ValueUint(((TypeUint) abiType).bitSize, Encoder.decodeBytesToUint(encoded));
        } else if (abiType instanceof TypeUfixed) {
            if (encoded.length != ((TypeUfixed) abiType).bitSize / 8)
                throw new IllegalArgumentException("encoded byte size not match with ufixed byte size");
            return new ValueUfixed((TypeUfixed) abiType, Encoder.decodeBytesToUint(encoded));
        } else if (abiType instanceof TypeAddress) {
            if (encoded.length != 32)
                throw new IllegalArgumentException("encode byte size not match with address byte size");
            return new ValueAddress(encoded);
        } else if (abiType instanceof TypeBool) {
            if (encoded.length != 1)
                throw new IllegalArgumentException("encode byte size not match with bool byte size");
            if (encoded[0] == 0x00)
                return new ValueBool(false);
            else if (encoded[0] == (byte) 0x80)
                return new ValueBool(true);
            else
                throw new IllegalArgumentException("encoded byte for bool is not either 0x80 or 0x00");
        } else if (abiType instanceof TypeByte) {
            if (encoded.length != 1)
                throw new IllegalArgumentException("encode byte size not match with byteT byte size");
            return new ValueByte(encoded[0]);
        } else if (abiType instanceof TypeString) {
            byte[] encodedLength = Value.getLengthEncoded(encoded);
            byte[] encodedString = Value.getContentEncoded(encoded);
            if (!Encoder.decodeBytesToUint(encodedLength).equals(BigInteger.valueOf(encodedString.length)))
                throw new IllegalArgumentException("string decode failure: encoded bytes do not match with length header");
            return new ValueString(new String(encodedString, StandardCharsets.UTF_8));
        } else if (abiType instanceof TypeArrayStatic) {
            TypeTuple castedTuple = castToTuple(((TypeArrayStatic) abiType).length, ((TypeArrayStatic) abiType).elemType);
            Value decodedCasted = Value.tupleDecoding(encoded, castedTuple);
            return new ValueArrayStatic((Value[]) decodedCasted.value);
        } else if (abiType instanceof TypeArrayDynamic) {
            byte[] encodedLength = Value.getLengthEncoded(encoded);
            byte[] encodedArray = Value.getContentEncoded(encoded);
            TypeTuple castedTuple = Value.castToTuple(Encoder.decodeBytesToUint(encodedLength).intValue(), ((TypeArrayDynamic) abiType).elemType);
            Value decodedCasted = Value.tupleDecoding(encodedArray, castedTuple);
            return new ValueArrayDynamic((Value[]) decodedCasted.value, ((TypeArrayDynamic) abiType).elemType);
        } else if (abiType instanceof TypeTuple) {
            return Value.tupleDecoding(encoded, (TypeTuple) abiType);
        }
        throw new IllegalArgumentException("abiType cannot be inferred, decode failed");
    }

    private static byte[] getLengthEncoded(byte[] encoded) {
        if (encoded.length < 2)
            throw new IllegalArgumentException("encode byte size too small, less than 2 bytes");
        byte[] encodedLength = new byte[2];
        System.arraycopy(encoded, 0, encodedLength, 0, 2);
        return encodedLength;
    }

    private static byte[] getContentEncoded(byte[] encoded) {
        if (encoded.length < 2)
            throw new IllegalArgumentException("encode byte size too small, less than 2 bytes");
        byte[] encodedString = new byte[encoded.length - 2];
        System.arraycopy(encoded, 2, encodedString, 0, encoded.length - 2);
        return encodedString;
    }

    private static TypeTuple castToTuple(int size, Type t) {
        List<Type> tupleTypes = new ArrayList<>();
        for (int i = 0; i < size; i++)
            tupleTypes.add(t);
        return new TypeTuple(tupleTypes);
    }

    private static Value tupleDecoding(byte[] encoded, TypeTuple castedType) {
        List<Integer> dynamicSeg = new ArrayList<>();
        List<byte[]> valuePartition = new ArrayList<>();
        int iterIndex = 0;

        for (int i = 0; i < castedType.childTypes.size(); i++) {
            if (castedType.childTypes.get(i).isDynamic()) {
                if (iterIndex + 2 > encoded.length)
                    throw new IllegalArgumentException("ill formed tuple dynamic typed element encoding: not enough bytes for index");
                byte[] encodedIndex = new byte[2];
                System.arraycopy(encoded, iterIndex, encodedIndex, 0, 2);
                int index = Encoder.decodeBytesToUint(encodedIndex).intValue();
                dynamicSeg.add(index);
                valuePartition.add(new byte[]{});
                iterIndex += 2;
            } else if (castedType.childTypes.get(i) instanceof TypeBool) {
                Type[] childTypeArr = castedType.childTypes.toArray(new Type[0]);
                int before = Type.findBoolLR(childTypeArr, i, -1);
                int after = Type.findBoolLR(childTypeArr, i, 1);
                if (before % 8 != 0)
                    throw new IllegalArgumentException("expected bool number mod 8 == 0");
                after = Math.min(after, 7);
                for (int boolIndex = 0; boolIndex <= after; boolIndex++) {
                    byte boolMask = (byte) (0x80 >> boolIndex);
                    byte append = ((encoded[iterIndex] & boolMask) != 0) ? (byte) 0x80 : 0x00;
                    valuePartition.add(new byte[]{append});
                }
                i += after;
                iterIndex++;
            } else {
                int expectedLen = castedType.childTypes.get(i).byteLen();
                if (iterIndex + expectedLen > encoded.length)
                    throw new IllegalArgumentException("ill formed tuple static typed element encoding: not enough bytes");
                byte[] partition = new byte[expectedLen];
                System.arraycopy(encoded, iterIndex, partition, 0, expectedLen);
                valuePartition.add(partition);
                iterIndex += expectedLen;
            }
            if (i != castedType.childTypes.size() - 1 && iterIndex >= encoded.length)
                throw new IllegalArgumentException("input bytes not enough to decode");
        }
        if (dynamicSeg.size() > 0) {
            dynamicSeg.add(encoded.length);
            iterIndex = encoded.length;
        }
        if (iterIndex < encoded.length)
            throw new IllegalArgumentException("input bytes not fully consumed");

        int indexTemp = -1;
        for (int var : dynamicSeg) {
            if (var >= indexTemp)
                indexTemp = var;
            else
                throw new IllegalArgumentException("dynamic segments should display a [l, r] scope where l <= r");
        }

        int segIndex = 0;
        for (int i = 0; i < castedType.childTypes.size(); i++) {
            if (castedType.childTypes.get(i).isDynamic()) {
                int length = dynamicSeg.get(segIndex + 1) - dynamicSeg.get(segIndex);
                byte[] partition = new byte[length];
                System.arraycopy(encoded, dynamicSeg.get(segIndex), partition, 0, length);
                valuePartition.set(i, partition);
                segIndex++;
            }
        }
        Value[] values = new Value[castedType.childTypes.size()];
        for (int i = 0; i < castedType.childTypes.size(); i++)
            values[i] = Value.decode(valuePartition.get(i), castedType.childTypes.get(i));
        return new ValueTuple(values);
    }
}
