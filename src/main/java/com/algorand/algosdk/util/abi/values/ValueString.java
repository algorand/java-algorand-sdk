package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.util.abi.types.TypeString;

import java.math.BigInteger;
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
        byte[] lengthEncode = Encoder.encodeUintToBytes(BigInteger.valueOf((long) buffer.length), 2);
        byte[] res = new byte[buffer.length + 2];
        System.arraycopy(lengthEncode, 0, res, 0, 2);
        System.arraycopy(buffer, 2, res, 0, buffer.length);
        return res;
    }
}
