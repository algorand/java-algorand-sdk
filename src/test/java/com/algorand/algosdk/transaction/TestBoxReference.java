package com.algorand.algosdk.transaction;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class TestBoxReference {

    private AppBoxReference genWithAppId(long appId) {
        return new AppBoxReference(appId, "example".getBytes(StandardCharsets.US_ASCII));
    }

    private AppBoxReference genWithNewAppId() {
        return new AppBoxReference(0L, "example".getBytes(StandardCharsets.US_ASCII));
    }

    @Test
    public void testAppIndexExists() {
        long appId = 7;
        AppBoxReference abr = genWithAppId(appId);

        Assert.assertEquals(
                new Transaction.BoxReference(4, abr.getName()),
                Transaction.BoxReference.fromAppBoxReference(
                        abr,
                        Lists.newArrayList(1L, 3L, 4L, appId),
                        appId - 1
                )
        );
    }

    @Test(expected = RuntimeException.class)
    public void testAppIndexDoesNotExist() {
        long appId = 7;
        AppBoxReference abr = genWithAppId(appId);

        Assert.assertEquals(
                new Transaction.BoxReference(4, abr.getName()),
                Transaction.BoxReference.fromAppBoxReference(
                        abr,
                        Lists.newArrayList(1L, 3L, 4L),
                        appId - 1
                )
        );
    }

    @Test
    public void testNewAppId() {
        AppBoxReference abr = genWithNewAppId();
        Assert.assertEquals(
                new Transaction.BoxReference(0, abr.getName()),
                Transaction.BoxReference.fromAppBoxReference(
                        abr, Lists.newArrayList(), 1L));
    }

    @Test
    public void testFallbackToCurrentApp() {
        // Mirrors priority search in goal from `cmd/goal/application.go::translateBoxRefs`.
        long appId = 7;
        AppBoxReference abr = genWithAppId(appId);

        // Prefer foreign apps index when present.
        Assert.assertEquals(
                new Transaction.BoxReference(4, abr.getName()),
                Transaction.BoxReference.fromAppBoxReference(
                        abr, Lists.newArrayList(1L, 3L, 4L, appId), appId));

        // Fallback to current app when absent from foreign apps.
        Assert.assertEquals(
                new Transaction.BoxReference(0, abr.getName()),
                Transaction.BoxReference.fromAppBoxReference(
                        abr, Lists.newArrayList(1L, 3L, 4L), appId));
    }

}
