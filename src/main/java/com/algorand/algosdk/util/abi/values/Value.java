package com.algorand.algosdk.util.abi.values;

import com.algorand.algosdk.util.abi.types.*;

import java.math.BigInteger;

public class Value {
    public Type abiType;
    public Object value;

    Value() {
    }

    private Value(Value o) {
        this.abiType = o.abiType;
        this.value = o.value;
    }

    public byte[] encode() throws IllegalAccessException {
        throw new IllegalAccessException("Should not access to base value method: encode");
    }

    public Value(byte[] encoded, Type abiType) throws IllegalArgumentException {
        this(Value.decode(encoded, abiType));
    }

    public static Value decode(byte[] encoded, Type abiType) {
        if (abiType instanceof UintT) {
            if (encoded.length != ((UintT) abiType).bitSize / 8)
                throw new IllegalArgumentException("encoded byte size not match with uint byte size");
            return new UintV(((UintT) abiType).bitSize, new BigInteger(encoded));
        } else if (abiType instanceof UfixedT) {
            if (encoded.length != ((UfixedT) abiType).bitSize / 8)
                throw new IllegalArgumentException("encoded byte size not match with ufixed byte size");
            return new UfixedV((UfixedT) abiType, new BigInteger(encoded));
        } else if (abiType instanceof AddressT) {
            if (encoded.length != 32)
                throw new IllegalArgumentException("encode byte size not match with address byte size");
            return new AddressV(encoded);
        } else if (abiType instanceof BoolT) {
            if (encoded.length != 1)
                throw new IllegalArgumentException("encode byte size not match with bool byte size");
            if (encoded[0] == 0x00)
                return new BoolV(false);
            else if (encoded[0] == (byte) 0x80)
                return new BoolV(true);
            else
                throw new IllegalArgumentException("encoded byte for bool is not either 0x80 or 0x00");
        } else if (abiType instanceof ByteT) {
            if (encoded.length != 1)
                throw new IllegalArgumentException("encode byte size not match with byteT byte size");
            return new ByteV(encoded[0]);
        } else if (abiType instanceof StringT) {
            if (encoded.length < 2)
                throw new IllegalArgumentException("encode byte size too small, less than 2 bytes");
        }
        // TODO implementation
        return new Value();
    }
}
