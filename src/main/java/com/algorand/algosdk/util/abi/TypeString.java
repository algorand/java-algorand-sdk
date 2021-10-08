package com.algorand.algosdk.util.abi;

import com.algorand.algosdk.util.Encoder;
import org.apache.commons.lang3.ArrayUtils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class TypeString extends Type {
    public TypeString() {
    }

    @Override
    public String toString() {
        return "string";
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TypeString;
    }

    @Override
    public byte[] encode(Object o) {
        if (!(o instanceof String))
            throw new IllegalArgumentException("cannot infer type for abi string encode");
        byte[] buffer = ((String) o).getBytes(StandardCharsets.UTF_8);
        if (buffer.length >= (1 << 16))
            throw new IllegalArgumentException("string casted to byte exceeds uint16 maximum, error");
        byte[] lengthEncode = Encoder.encodeUintToBytes(BigInteger.valueOf(buffer.length), ABI_DYNAMIC_HEAD_BYTE_LEN);
        byte[] castedBytes = Type.castToTupleType(buffer.length, new TypeByte()).encode(ArrayUtils.toObject(buffer));

        ByteBuffer bf = ByteBuffer.allocate(castedBytes.length + ABI_DYNAMIC_HEAD_BYTE_LEN);
        bf.put(lengthEncode);
        bf.put(castedBytes);
        return bf.array();
    }

    @Override
    public Object decode(byte[] encoded) {
        byte[] encodedLength = Type.getLengthEncoded(encoded);
        byte[] encodedString = Type.getContentEncoded(encoded);
        if (!Encoder.decodeBytesToUint(encodedLength).equals(BigInteger.valueOf(encodedString.length)))
            throw new IllegalArgumentException("string decode failure: encoded bytes do not match with length header");
        return new String(encodedString, StandardCharsets.UTF_8);
    }

    @Override
    public int byteLen() {
        throw new IllegalArgumentException("Dynamic type cannot pre-compute byteLen");
    }
}
