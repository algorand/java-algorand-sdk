package com.algorand.algosdk.util;

import com.algorand.algosdk.transaction.Transaction;

import java.util.Random;

public class Lease {
    public static final int LEASE_LENGTH = 32;

    /**
     * Validates a lease byte array. The lease should be an empty array or a 32 byte array.
     * @param value a lease byte array.
     * @return true if the lease is valid, otherwise false.
     */
    public static boolean valid(byte[] value) {
        return value.length == LEASE_LENGTH || value.length == 0;
    }

    /**
     * Creates a randomized lease.
     * @return a randomized lease
     */
    public static byte[] makeRandomLease() {
        byte[] result = new byte[LEASE_LENGTH];
        new Random().nextBytes(result);
        return result;
    }
}
