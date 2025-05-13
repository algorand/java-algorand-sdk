package com.algorand.algosdk.abi;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.util.GenericObjToArray;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TypeTuple extends ABIType {
    public final List<ABIType> childTypes;

    public TypeTuple(List<ABIType> childTypes) {
        this.childTypes = childTypes;
    }

    @Override
    public String toString() {
        List<String> childStrs = new ArrayList<>();
        for (ABIType t : this.childTypes)
            childStrs.add(t.toString());
        return "(" + StringUtils.join(childStrs.toArray(new String[0]), ",") + ")";
    }

    @Override
    public boolean isDynamic() {
        for (ABIType t : this.childTypes) {
            if (t.isDynamic())
                return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TypeTuple)) return false;
        return this.childTypes.equals(((TypeTuple) o).childTypes);
    }

    @Override
    public byte[] encode(Object o) {
        Object[] tupleValues = GenericObjToArray.unifyToArrayOfObjects(o);

        if (tupleValues.length != this.childTypes.size())
            throw new IllegalArgumentException("abi tuple child type size != abi tuple element value size");

        ABIType[] tupleTypes = this.childTypes.toArray(new ABIType[0]);

        List<byte[]> heads = new ArrayList<>(tupleTypes.length);
        List<byte[]> tails = new ArrayList<>(tupleTypes.length);
        for (int i = 0; i < tupleTypes.length; i++) {
            heads.add(new byte[]{});
            tails.add(new byte[]{});
        }
        Set<Integer> dynamicIndex = new HashSet<>();

        for (int i = 0; i < tupleTypes.length; i++) {
            ABIType currType = tupleTypes[i];
            Object currValue = tupleValues[i];
            byte[] currHead, currTail;

            if (currType.isDynamic()) {
                currHead = new byte[]{0x00, 0x00};
                currTail = currType.encode(currValue);
                dynamicIndex.add(i);
            } else if (currType instanceof TypeBool) {
                int before = ABIType.findBoolLR(tupleTypes, i, -1);
                int after = ABIType.findBoolLR(tupleTypes, i, 1);
                if (before % 8 != 0)
                    throw new IllegalArgumentException("expected before has number of bool mod 8 == 0");
                after = Math.min(after, 7);

                byte compressed = 0;
                for (int boolIndex = 0; boolIndex <= after; boolIndex++) {
                    if ((boolean) tupleValues[i + boolIndex])
                        compressed |= ((byte) 1) << (7 - boolIndex);
                }

                currHead = new byte[]{compressed};
                currTail = new byte[]{};
                i += after;
            } else {
                currHead = currType.encode(currValue);
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
                heads.set(i, Encoder.encodeUintToBytes(BigInteger.valueOf(headValue), ABI_DYNAMIC_HEAD_BYTE_LEN));
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
    public Object decode(byte[] encoded) {
        List<Integer> dynamicSeg = new ArrayList<>();
        List<byte[]> valuePartition = new ArrayList<>();
        int iterIndex = 0;

        for (int i = 0; i < this.childTypes.size(); i++) {
            if (this.childTypes.get(i).isDynamic()) {
                if (iterIndex + ABI_DYNAMIC_HEAD_BYTE_LEN > encoded.length)
                    throw new IllegalArgumentException("ill formed tuple dynamic typed element encoding: not enough bytes for index");
                byte[] encodedIndex = new byte[ABI_DYNAMIC_HEAD_BYTE_LEN];
                System.arraycopy(encoded, iterIndex, encodedIndex, 0, ABI_DYNAMIC_HEAD_BYTE_LEN);
                int index = Encoder.decodeBytesToUint(encodedIndex).intValue();
                dynamicSeg.add(index);
                valuePartition.add(new byte[]{});
                iterIndex += ABI_DYNAMIC_HEAD_BYTE_LEN;
            } else if (this.childTypes.get(i) instanceof TypeBool) {
                ABIType[] childTypeArr = this.childTypes.toArray(new ABIType[0]);
                int before = ABIType.findBoolLR(childTypeArr, i, -1);
                int after = ABIType.findBoolLR(childTypeArr, i, 1);
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
                int expectedLen = this.childTypes.get(i).byteLen();
                if (iterIndex + expectedLen > encoded.length)
                    throw new IllegalArgumentException("ill formed tuple static typed element encoding: not enough bytes");
                byte[] partition = new byte[expectedLen];
                System.arraycopy(encoded, iterIndex, partition, 0, expectedLen);
                valuePartition.add(partition);
                iterIndex += expectedLen;
            }
            if (i != this.childTypes.size() - 1 && iterIndex >= encoded.length)
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
        for (int i = 0; i < this.childTypes.size(); i++) {
            if (this.childTypes.get(i).isDynamic()) {
                int length = dynamicSeg.get(segIndex + 1) - dynamicSeg.get(segIndex);
                byte[] partition = new byte[length];
                System.arraycopy(encoded, dynamicSeg.get(segIndex), partition, 0, length);
                valuePartition.set(i, partition);
                segIndex++;
            }
        }
        Object[] values = new Object[this.childTypes.size()];
        for (int i = 0; i < this.childTypes.size(); i++)
            values[i] = this.childTypes.get(i).decode(valuePartition.get(i));
        return values;
    }

    @Override
    public int byteLen() {
        int size = 0;
        for (int i = 0; i < this.childTypes.size(); i++) {
            if (this.childTypes.get(i) instanceof TypeBool) {
                int after = ABIType.findBoolLR(this.childTypes.toArray(new ABIType[0]), i, 1);
                i += after;
                int boolNumber = after + 1;
                size += boolNumber / 8;
                size += (boolNumber % 8 != 0) ? 1 : 0;
            } else
                size += this.childTypes.get(i).byteLen();
        }
        return size;
    }
}
