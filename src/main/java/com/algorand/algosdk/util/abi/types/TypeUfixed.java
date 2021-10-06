package com.algorand.algosdk.util.abi.types;

import java.util.Objects;

public class TypeUfixed extends Type {
    public final int bitSize;
    public final int precision;

    public TypeUfixed(int size, int precision) {
        if (size < 8 || size > 512 || size % 8 != 0)
            throw new IllegalArgumentException(
                    "ufixed initialize failure: bitSize should be in [8, 512] and bitSize mod 8 == 0"
            );
        if (precision < 1 || precision > 160)
            throw new IllegalArgumentException("ufixed initialize failure: precision should be in [1, 160]");
        this.bitSize = size;
        this.precision = precision;
    }

    @Override
    public String toString() {
        return "ufixed" + this.bitSize + "x" + this.precision;
    }

    public boolean isDynamic() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TypeUfixed)) return false;
        return bitSize == ((TypeUfixed) o).bitSize && precision == ((TypeUfixed) o).precision;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bitSize, precision);
    }

    public int byteLen() {
        return this.bitSize / 8;
    }
}
