package com.algorand.algosdk.transaction;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestLease {

    @Test
    public void equalLeasesShouldHaveEqualHashCodes() {
        byte[] firstBytes = new byte[Lease.LEASE_LENGTH];
        byte[] secondBytes = new byte[Lease.LEASE_LENGTH];

        firstBytes[0] = 1;
        secondBytes[0] = 1;

        Lease first = new Lease(firstBytes);
        Lease second = new Lease(secondBytes);

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }
}

