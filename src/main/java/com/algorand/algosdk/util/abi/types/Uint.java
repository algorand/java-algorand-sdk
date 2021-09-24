package com.algorand.algosdk.util.abi.types;

import java.util.Objects;

class Uint extends Type {
    public final int bitSize;

    Uint(int size) {
        if (size < 8 || size > 512 || size % 8 != 0)
            throw new IllegalArgumentException(
                    "uint initialize failure: bitSize should be in [8, 512] and bitSize moe 8 = 0"
            );
        this.bitSize = size;
    }

    @Override
    public String string() { return "uint" + this.bitSize; }

    @Override
    public boolean isDynamic() { return false; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Uint)) return false;
        return this.bitSize == ((Uint) o).bitSize;
    }

    @Override
    public int hashCode() { return Objects.hash(bitSize); }
}
