package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.util.abi.types.Type;
import com.algorand.algosdk.util.abi.types.TypeArrayDynamic;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ValueArrayDynamic extends Value {
    public ValueArrayDynamic(Value[] val, Type elemType) {
        if (val.length >= (1 << 16))
            throw new IllegalArgumentException("Cannot build dynamic array: array length >= 2^16");
        for (Value v : val) {
            if (!v.abiType.equals(elemType))
                throw new IllegalArgumentException("Cannot build dynamic array: array element type not matching");
        }
        this.value = val;
        this.abiType = new TypeArrayDynamic(elemType);
    }

    public byte[] encode() {
        byte[] castedEncode = new ValueTuple((Value[]) this.value).encode();
        ByteBuffer bf = ByteBuffer.allocate(castedEncode.length + 2);
        byte[] lengthEncode = Encoder.encodeUintToBytes(BigInteger.valueOf(((Value[]) this.value).length), 2);
        bf.put(lengthEncode);
        bf.put(castedEncode);
        return bf.array();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ValueArrayDynamic))
            return false;
        if (!this.abiType.equals(((ValueArrayDynamic) obj).abiType))
            return false;
        return Arrays.equals((Value[]) this.value, (Value[]) ((ValueArrayDynamic) obj).value);
    }
}
