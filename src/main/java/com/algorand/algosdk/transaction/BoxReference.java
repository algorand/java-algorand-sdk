package com.algorand.algosdk.transaction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.List;

public class BoxReference {
    // the appID of the app this box belongs to. Instead of serializing this value,
    // it's used to calculate the appIdx for for BoxReferenceSerialize.
    public Long appID;

    // the name of the box unique to the app it belongs to
    public byte[] name;

    public BoxReference(Long appID, byte[] name) {
        this.appID = appID;
        this.name = name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoxReference that = (BoxReference) o;
        return appID.equals(that.appID) &&
            name.equals(that.name);
    }

    public BoxReferenceSerialize getBoxReferenceSerialize(List<Long> foreignApps, Long curApp) {
        if (appID == 0) {
            return new BoxReferenceSerialize(0, name);
        }

        int appIdx = -1;
        if (appID == curApp) {
            appIdx = 0;
        }

        for (int i = 0; i < foreignApps.size(); i++) {
            if (foreignApps.get(i).equals(appID)) {
                appIdx = i + 1;
                break;
            }
        }

        if (appIdx == -1) {
            throw new RuntimeException(String.format("this box's appID is not present in the foreign apps array: %d %d %s", appID, curApp, foreignApps));
        }
        return new BoxReferenceSerialize(appIdx, name);
    }

    @JsonPropertyOrder(alphabetic=true)
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public class BoxReferenceSerialize implements Serializable {
        // the index in the foreign apps array of the app this box belongs to
        @JsonProperty("i")
        public int appIdx;

        // the name of the box unique to the app it belongs to
        @JsonProperty("n")
        public byte[] name;

        @JsonCreator
        public BoxReferenceSerialize(@JsonProperty("i") int appIdx,
                            @JsonProperty("n") byte[] name) {
            this.appIdx = appIdx;
            this.name = name;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BoxReferenceSerialize that = (BoxReferenceSerialize) o;
            return appIdx == that.appIdx && name.equals(that.name);
        }
    }
}
