package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Genesis extends PathResponse {

    @JsonProperty("alloc")
    public List<GenesisAllocation> alloc = new ArrayList<GenesisAllocation>();

    @JsonProperty("comment")
    public String comment;

    @JsonProperty("devmode")
    public Boolean devmode;

    @JsonProperty("fees")
    public String fees;

    @JsonProperty("id")
    public String id;

    @JsonProperty("network")
    public String network;

    @JsonProperty("proto")
    public String proto;

    @JsonProperty("rwd")
    public String rwd;

    @JsonProperty("timestamp")
    public number timestamp;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        Genesis other = (Genesis) o;
        if (!Objects.deepEquals(this.alloc, other.alloc)) return false;
        if (!Objects.deepEquals(this.comment, other.comment)) return false;
        if (!Objects.deepEquals(this.devmode, other.devmode)) return false;
        if (!Objects.deepEquals(this.fees, other.fees)) return false;
        if (!Objects.deepEquals(this.id, other.id)) return false;
        if (!Objects.deepEquals(this.network, other.network)) return false;
        if (!Objects.deepEquals(this.proto, other.proto)) return false;
        if (!Objects.deepEquals(this.rwd, other.rwd)) return false;
        if (!Objects.deepEquals(this.timestamp, other.timestamp)) return false;

        return true;
    }
}
