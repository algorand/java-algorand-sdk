package com.algorand.algosdk.util;

import java.util.Arrays;

public class ComparableBytes {
    private final byte[] data;

    public ComparableBytes(byte[] data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ComparableBytes)) {
            return false;
        }
        return Arrays.equals(data, ((ComparableBytes)other).data);
    }
}
