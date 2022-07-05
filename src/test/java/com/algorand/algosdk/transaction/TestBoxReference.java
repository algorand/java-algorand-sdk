package com.algorand.algosdk.transaction;

import com.algorand.algosdk.v2.client.model.Box;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;

public class TestBoxReference {

    private BoxReference genConstant() {
        return new BoxReference(Long.valueOf(5), "example".getBytes(StandardCharsets.US_ASCII));
    }

    private BoxReference genWithAppId(long appId) {
        return new BoxReference(appId, "example".getBytes(StandardCharsets.US_ASCII));
    }

    private BoxReference genWithNewAppId() {
        return new BoxReference(0L, "example".getBytes(StandardCharsets.US_ASCII));
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
    public void testBoxReferenceSerializeAppIndexExists() {
        long appId = 7;
        BoxReference br = genWithAppId(appId);

        Assert.assertEquals(
                new BoxReference.BoxReferenceSerialize(4, br.name),
                br.getBoxReferenceSerialize(Lists.newArrayList(1L, 3L, 4L, appId), appId - 1));
    }

    @Test(expected = RuntimeException.class)
    public void testBoxReferenceSerializeAppIndexDoesNotExist() {
        long appId = 7;
        final BoxReference br = genWithAppId(appId);

        br.getBoxReferenceSerialize(Lists.newArrayList(1L, 3L, 4L), appId - 1);
    }

    @Test
    public void testBoxReferenceSerializeNewAppId() {
        BoxReference br = genWithNewAppId();
        Assert.assertEquals(
                new BoxReference.BoxReferenceSerialize(0, br.name),
                br.getBoxReferenceSerialize(Lists.newArrayList(), 1L));
    }
}
