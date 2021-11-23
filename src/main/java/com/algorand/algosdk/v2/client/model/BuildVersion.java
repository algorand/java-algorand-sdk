package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.*;

public class BuildVersion extends PathResponse {

    @JsonProperty("branch")
    public String branch;

    @JsonProperty("build_number")
    public Long build_number;

    @JsonProperty("channel")
    public String channel;

    @JsonProperty("commit_hash")
    public String commit_hash;

    @JsonProperty("major")
    public Long major;

    @JsonProperty("minor")
    public Long minor;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        BuildVersion other = (BuildVersion) o;
        if (!Objects.deepEquals(this.branch, other.branch)) return false;
        if (!Objects.deepEquals(this.build_number, other.build_number)) return false;
        if (!Objects.deepEquals(this.channel, other.channel)) return false;
        if (!Objects.deepEquals(this.commit_hash, other.commit_hash)) return false;
        if (!Objects.deepEquals(this.major, other.major)) return false;
        if (!Objects.deepEquals(this.minor, other.minor)) return false;

        return true;
    }
}
