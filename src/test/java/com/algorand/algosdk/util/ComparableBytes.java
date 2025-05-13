package com.algorand.algosdk.util;

import org.apache.commons.codec.binary.Hex;

import java.util.Arrays;

public class ComparableBytes {
    private final byte[] data;

    public ComparableBytes(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return Hex.encodeHexString(data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComparableBytes that = (ComparableBytes) o;
        return Arrays.equals(data, that.data);
    }
}
