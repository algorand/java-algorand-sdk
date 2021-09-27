package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.abi.types.StringT;

import java.nio.charset.StandardCharsets;

public class StringV extends Value {
    public StringV(String str) {
        this.abiType = new StringT();
        this.value = str;
    }

    @Override
    public byte[] encode() throws IllegalAccessException {
        byte[] buffer = ((String) this.value).getBytes(StandardCharsets.UTF_8);
        int bufferLen = buffer.length;
        if (bufferLen >= (1 << 16))
            throw new IllegalArgumentException("string casted to byte exceeds uint16 maximum, error");
        byte[] res = new byte[bufferLen + 2];
        System.arraycopy(buffer, 0, res, 2, bufferLen);

    }
}
