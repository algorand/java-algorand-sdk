package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.util.abi.types.TypeString;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ValueString extends Value {
    public ValueString(String str) {
        this.abiType = new TypeString();
        this.value = str;
    }

    public byte[] encode() {
        byte[] buffer = ((String) this.value).getBytes(StandardCharsets.UTF_8);
        if (buffer.length >= (1 << 16))
            throw new IllegalArgumentException("string casted to byte exceeds uint16 maximum, error");
        byte[] lengthEncode = Encoder.encodeUintToBytes(BigInteger.valueOf(buffer.length), 2);

        Value[] values = new Value[buffer.length];
        for (int i = 0; i < buffer.length; i++)
            values[i] = new ValueByte(buffer[i]);
        byte[] castedBytes = new ValueTuple(values).encode();
        ByteBuffer bf = ByteBuffer.allocate(castedBytes.length + 2);
        bf.put(lengthEncode);
        bf.put(castedBytes);
        return bf.array();
    }
}
