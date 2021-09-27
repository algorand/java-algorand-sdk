package com.algorand.algosdk.util.abi.types;

import java.util.Objects;

public class UintT extends Type {
    public final int bitSize;

    public UintT(int size) {
        if (size < 8 || size > 512 || size % 8 != 0)
            throw new IllegalArgumentException(
                    "uint initialize failure: bitSize should be in [8, 512] and bitSize moe 8 = 0"
            );
        this.bitSize = size;
    }

    @Override
    public String string() {
        return "uint" + this.bitSize;
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UintT)) return false;
        return this.bitSize == ((UintT) o).bitSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bitSize);
    }

    @Override
    public int byteLen() throws IllegalAccessException {
        return this.bitSize / 8;
    }
}
