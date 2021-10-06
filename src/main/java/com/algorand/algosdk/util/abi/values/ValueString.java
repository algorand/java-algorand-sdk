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

    public ValueString(byte[] encoded) {
        byte[] encodedLength = Value.getLengthEncoded(encoded);
        byte[] encodedString = Value.getContentEncoded(encoded);
        if (!Encoder.decodeBytesToUint(encodedLength).equals(BigInteger.valueOf(encodedString.length)))
            throw new IllegalArgumentException("string decode failure: encoded bytes do not match with length header");
        this.abiType = new TypeString();
        this.value = new String(encodedString, StandardCharsets.UTF_8);
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ValueString))
            return false;
        if (!this.abiType.equals(((ValueString) obj).abiType))
            return false;
        return this.value.equals(((ValueString) obj).value);
    }
}
