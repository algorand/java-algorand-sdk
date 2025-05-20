package com.algorand.algosdk.v2.client.model;

import java.util.HashMap;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GenesisAllocation extends PathResponse {

    @JsonProperty("addr")
    public String addr;

    @JsonProperty("comment")
    public String comment;

    @JsonProperty("state")
    public HashMap<String,Object> state;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        GenesisAllocation other = (GenesisAllocation) o;
        if (!Objects.deepEquals(this.addr, other.addr)) return false;
        if (!Objects.deepEquals(this.comment, other.comment)) return false;
        if (!Objects.deepEquals(this.state, other.state)) return false;

        return true;
    }
}
