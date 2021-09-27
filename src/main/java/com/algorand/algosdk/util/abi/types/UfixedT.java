package com.algorand.algosdk.util.abi.types;

import java.util.Objects;

class UfixedT extends Type {
    public final int bitSize;
    public final int precision;

    UfixedT(int size, int precision) {
        if (size < 8 || size > 512 || size % 8 != 0)
            throw new IllegalArgumentException(
                    "ufixed initialize failure: bitSize should be in [8, 512] and bitSize moe 8 = 0"
            );
        if (precision < 1 || precision > 160)
            throw new IllegalArgumentException("ufixed initialize failure: precision should be in [1, 160]");
        this.bitSize = size;
        this.precision = precision;
    }

    @Override
    public String string() {
        return "ufixed" + this.bitSize + "x" + this.precision;
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UfixedT)) return false;
        return bitSize == ((UfixedT) o).bitSize && precision == ((UfixedT) o).precision;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bitSize, precision);
    }

    @Override
    public int byteLen() throws IllegalAccessException {
        return this.bitSize / 8;
    }
}
