package com.algorand.algosdk.transaction;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class TestAppBoxReference {

    private AppBoxReference genConstant() {
        return new AppBoxReference(Long.valueOf(5), "example".getBytes(StandardCharsets.US_ASCII));
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
