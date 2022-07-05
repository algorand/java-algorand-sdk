package com.algorand.algosdk.transaction;

import java.util.Arrays;
import java.util.Objects;

public class AppBoxReference {
    // the app ID of the app this box belongs to. Instead of serializing this value,
    // it's used to calculate the appIdx for BoxReferenceSerialize.
    private final long appId;

    // the name of the box unique to the app it belongs to
    private final byte[] name;

    public AppBoxReference(long appId, byte[] name) {
        this.appId = appId;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppBoxReference that = (AppBoxReference) o;
        return appId == that.appId && Arrays.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(appId);
        result = 31 * result + Arrays.hashCode(name);
        return result;
    }

    public long getAppId() {
        return appId;
    }

    public byte[] getName() {
        return Arrays.copyOf(name, name.length);
    }

    @Override
    public String toString() {
        return "BoxReference{" +
                "appID=" + appId +
                ", name=" + Arrays.toString(name) +
                '}';
    }
}
