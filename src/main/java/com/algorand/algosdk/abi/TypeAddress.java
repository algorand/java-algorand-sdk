package com.algorand.algosdk.abi;

import com.algorand.algosdk.crypto.Address;
import org.apache.commons.lang3.ArrayUtils;

public class TypeAddress extends Type {
    public TypeAddress() {
    }

    @Override
    public String toString() {
        return "address";
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TypeAddress;
    }

    @Override
    public byte[] encode(Object obj) {
        byte[] values;
        if (obj instanceof byte[] && ((byte[]) obj).length == Address.LEN_BYTES)
            values = (byte[]) obj;
        else if (obj instanceof Address)
            values = ((Address) obj).getBytes();
        else
            throw new IllegalArgumentException("cannot infer type for abi encoding in address");

        return Type.castToTupleType(Address.LEN_BYTES, new TypeByte()).encode(ArrayUtils.toObject(values));
    }

    @Override
    public Object decode(byte[] encoded) {
        if (encoded.length != Address.LEN_BYTES)
            throw new IllegalArgumentException("cannot decode abi address, address byte length should be 32");
        return new Address(encoded);
    }

    @Override
    public int byteLen() {
        return Address.LEN_BYTES;
    }
}
