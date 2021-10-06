package com.algorand.algosdk.util.abi.types;

import java.util.Objects;

public class TypeUint extends Type {
    public final int bitSize;

    public TypeUint(int size) {
        if (size < 8 || size > 512 || size % 8 != 0)
            throw new IllegalArgumentException(
                    "uint initialize failure: bitSize should be in [8, 512] and bitSize mod 8 == 0"
            );
        this.bitSize = size;
    }

    @Override
    public String toString() {
        return "uint" + this.bitSize;
    }

    public boolean isDynamic() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TypeUint)) return false;
        return this.bitSize == ((TypeUint) o).bitSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bitSize);
    }

    public int byteLen() {
        return this.bitSize / 8;
    }
}
