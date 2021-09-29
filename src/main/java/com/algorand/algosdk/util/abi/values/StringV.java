package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.abi.types.StringT;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class StringV extends Value {
    public StringV(String str) {
        this.abiType = new StringT();
        this.value = str;
    }

    @Override
    public byte[] encode() throws IllegalAccessException {
        byte[] buffer = ((String) this.value).getBytes(StandardCharsets.UTF_8);
        if (buffer.length >= (1 << 16))
            throw new IllegalArgumentException("string casted to byte exceeds uint16 maximum, error");
        byte[] lengthEncode = Value.encodeUintToBytes(BigInteger.valueOf((long) buffer.length), 2);
        byte[] res = new byte[buffer.length + 2];
        System.arraycopy(lengthEncode, 0, res, 0, 2);
        System.arraycopy(buffer, 2, res, 0, buffer.length);
        return res;
    }
}
