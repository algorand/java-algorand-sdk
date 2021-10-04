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
}
