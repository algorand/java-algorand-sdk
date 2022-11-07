package com.algorand.algosdk.transaction;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class TestAppBoxReference {

    private AppBoxReference genConstant() {
        return new AppBoxReference(5, "example".getBytes(StandardCharsets.US_ASCII));
    }

    @Test
    public void testEqualsByReference() {
        Assert.assertEquals(genConstant(), genConstant());
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(genConstant().hashCode(), genConstant().hashCode());
    }

    @Test
    public void testNameQueryEncoding() {
        String example = "tkÿÿ";
        String expectedEncoding = "b64:dGvDv8O/";

        AppBoxReference abr = new AppBoxReference(0, example.getBytes(StandardCharsets.UTF_8));

        Assert.assertEquals(
                expectedEncoding,
                abr.nameQueryEncoded()
        );
    }
}
