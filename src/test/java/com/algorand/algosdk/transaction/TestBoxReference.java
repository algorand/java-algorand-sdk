package com.algorand.algosdk.transaction;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import org.junit.Assert;

public class TestBoxReference {

    private BoxReference genConstant() {
        return new BoxReference(Long.valueOf(5), "example".getBytes(StandardCharsets.US_ASCII));
    }

    @Test
    public void testEqualsByReference() {
        Assert.assertEquals(genConstant(), genConstant());
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(genConstant().hashCode(), genConstant().hashCode());
    }
}
