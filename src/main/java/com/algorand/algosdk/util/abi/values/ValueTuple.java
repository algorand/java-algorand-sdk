package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.util.abi.types.TypeBool;
import com.algorand.algosdk.util.abi.types.TypeTuple;
import com.algorand.algosdk.util.abi.types.Type;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.*;

public class ValueTuple extends Value {
    public ValueTuple(Value[] val) {
        if (val.length >= (1 << 16))
            throw new IllegalArgumentException("Cannot build tuple: element array length >= 2^16");
        Type[] elemTypes = new Type[val.length];
        for (int i = 0; i < val.length; i++)
            elemTypes[i] = val[i].abiType;
        this.abiType = new TypeTuple(Arrays.asList(elemTypes));
        this.value = val;
    }

    public ValueTuple(Type[] types, byte[] encoded) {
        this(new TypeTuple(Arrays.asList(types)), encoded);
    }

    public ValueTuple(TypeTuple tupleT, byte[] encoded) {
        List<Integer> dynamicSeg = new ArrayList<>();
        List<byte[]> valuePartition = new ArrayList<>();
        int iterIndex = 0;

        for (int i = 0; i < tupleT.childTypes.size(); i++) {
            if (tupleT.childTypes.get(i).isDynamic()) {
                if (iterIndex + 2 > encoded.length)
                    throw new IllegalArgumentException("ill formed tuple dynamic typed element encoding: not enough bytes for index");
                byte[] encodedIndex = new byte[2];
                System.arraycopy(encoded, iterIndex, encodedIndex, 0, 2);
                int index = Encoder.decodeBytesToUint(encodedIndex).intValue();
                dynamicSeg.add(index);
                valuePartition.add(new byte[]{});
                iterIndex += 2;
            } else if (tupleT.childTypes.get(i) instanceof TypeBool) {
                Type[] childTypeArr = tupleT.childTypes.toArray(new Type[0]);
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
                int expectedLen = tupleT.childTypes.get(i).byteLen();
                if (iterIndex + expectedLen > encoded.length)
                    throw new IllegalArgumentException("ill formed tuple static typed element encoding: not enough bytes");
                byte[] partition = new byte[expectedLen];
                System.arraycopy(encoded, iterIndex, partition, 0, expectedLen);
                valuePartition.add(partition);
                iterIndex += expectedLen;
            }
            if (i != tupleT.childTypes.size() - 1 && iterIndex >= encoded.length)
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
        for (int i = 0; i < tupleT.childTypes.size(); i++) {
            if (tupleT.childTypes.get(i).isDynamic()) {
                int length = dynamicSeg.get(segIndex + 1) - dynamicSeg.get(segIndex);
                byte[] partition = new byte[length];
                System.arraycopy(encoded, dynamicSeg.get(segIndex), partition, 0, length);
                valuePartition.set(i, partition);
                segIndex++;
            }
        }
        Value[] values = new Value[tupleT.childTypes.size()];
        for (int i = 0; i < tupleT.childTypes.size(); i++)
            values[i] = Value.decode(valuePartition.get(i), tupleT.childTypes.get(i));

        this.value = values;
        this.abiType = tupleT;
    }

    public byte[] encode() {
        if (((Value[]) this.value).length >= (1 << 16))
            throw new IllegalArgumentException("Cannot build tuple: element array length >= 2^16");
        if (((Value[]) this.value).length != ((TypeTuple) this.abiType).childTypes.size())
            throw new IllegalArgumentException("Tuple Value child type number unmatch with tuple argument number");

        Type[] tupleTypes = ((TypeTuple) this.abiType).childTypes.toArray(new Type[0]);
        Value[] tupleValues = (Value[]) this.value;

        List<byte[]> heads = new ArrayList<>(tupleTypes.length);
        List<byte[]> tails = new ArrayList<>(tupleTypes.length);
        for (int i = 0; i < tupleTypes.length; i++) {
            heads.add(new byte[]{});
            tails.add(new byte[]{});
        }
        Set<Integer> dynamicIndex = new HashSet<>();

        for (int i = 0; i < tupleTypes.length; i++) {
            Type currType = tupleTypes[i];
            Value currValue = tupleValues[i];
            byte[] currHead, currTail;

            if (currType.isDynamic()) {
                currHead = new byte[]{0x00, 0x00};
                currTail = currValue.encode();
                dynamicIndex.add(i);
            } else if (currType instanceof TypeBool) {
                int before = Type.findBoolLR(tupleTypes, i, -1);
                int after = Type.findBoolLR(tupleTypes, i, 1);
                if (before % 8 != 0)
                    throw new IllegalArgumentException("expected before has number of bool mod 8 == 0");
                after = Math.min(after, 7);

                byte compressed = 0;
                for (int boolIndex = 0; boolIndex <= after; boolIndex++) {
                    if ((boolean) tupleValues[i + boolIndex].value)
                        compressed |= ((byte) 1) << (7 - boolIndex);
                }

                currHead = new byte[]{compressed};
                currTail = new byte[]{};
                i += after;
            } else {
                currHead = currValue.encode();
                currTail = new byte[]{};
            }
            heads.set(i, currHead);
            tails.set(i, currTail);
        }

        int headLength = 0;
        for (byte[] h : heads)
            headLength += h.length;

        int tailCurrLength = 0;
        for (int i = 0; i < heads.size(); i++) {
            if (dynamicIndex.contains(i)) {
                int headValue = headLength + tailCurrLength;
                if (headValue >= (1 << 16))
                    throw new IllegalArgumentException("encoding error: byte length >= 2^16");
                heads.set(i, Encoder.encodeUintToBytes(BigInteger.valueOf(headValue), 2));
            }
            tailCurrLength += tails.get(i).length;
        }
        ByteBuffer bf = ByteBuffer.allocate(headLength + tailCurrLength);
        for (byte[] h : heads)
            bf.put(h);
        for (byte[] t : tails)
            bf.put(t);
        return bf.array();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ValueTuple))
            return false;
        return Arrays.equals((Value[]) this.value, (Value[]) ((ValueTuple) obj).value);
    }
}
