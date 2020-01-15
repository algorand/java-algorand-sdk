package com.algorand.algosdk.transaction;

import com.algorand.algosdk.util.Encoder;
import com.fasterxml.jackson.annotation.*;

import java.util.Arrays;
import java.util.Random;

/**
 * Lease parameter, no transaction may also acquire this lease until lastValid.
 */
public class Lease {
    public static final int LEASE_LENGTH = 32;
    private final byte[] data;

    /**
     * Creates a randomly generated lease.
     */
    public Lease() {
        this(makeRandomLease());
    }

    /**
     * Creates a lease from a base64 encoded string.
     * @param lease base64 encoded string.
     */
    public Lease(String lease) {
        this(Encoder.decodeFromBase64(lease));
    }

    /**
     * Creates a lease from the provided bytes.
     * @param lease
     */
    @JsonCreator
    public Lease(byte[] lease) {
        if (!valid(lease)) {
            throw new IllegalArgumentException("Leases should be 0 or 32 bytes long, received: " + lease.length + " bytes");
        }

        this.data = lease;
    }

    @JsonValue
    public byte[] getBytes() {
        return Arrays.copyOf(data, data.length);
    }

    @Override
    public String toString() {
        return Encoder.encodeToBase64(this.data);
    }

    /**
     * Validates a lease byte array. The lease should be an empty array or a 32 byte array.
     * @param value a lease byte array.
     * @return true if the lease is valid, otherwise false.
     */
    private static boolean valid(byte[] value) {
        return value.length == LEASE_LENGTH || value.length == 0;
    }

    /**
     * Creates a randomized lease.
     * @return a randomized lease
     */
    private static byte[] makeRandomLease() {
        byte[] result = new byte[LEASE_LENGTH];
        new Random().nextBytes(result);
        return result;
    }
}
