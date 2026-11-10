package com.algorand.algosdk.transaction;

import java.util.HashSet;
import java.util.Set;

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

    @Test
     public void equalLeasesShouldWorkInHashSet() {
        byte[] bytes = new byte[Lease.LEASE_LENGTH];
        bytes[0] = 1;

        Set<Lease> set = new HashSet<>();
        set.add(new Lease(bytes.clone()));

        assertThat(set).contains(new Lease(bytes.clone()));
    }
}

