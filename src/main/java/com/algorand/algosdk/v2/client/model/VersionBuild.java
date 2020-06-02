package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * the current algod build version information. 
 */
public class VersionBuild extends PathResponse {

    @JsonProperty("branch")
    public String branch;

    @JsonProperty("build-number")
    public Long buildNumber;

    @JsonProperty("channel")
    public String channel;

    @JsonProperty("commit-hash")
    public void commitHash(String base64Encoded) {
        this.commitHash = Encoder.decodeFromBase64(base64Encoded);
    }
    @JsonProperty("commit-hash")
    public String commitHash() {
        return Encoder.encodeToBase64(this.commitHash);
    }
    public byte[] commitHash;

    @JsonProperty("major")
    public Long major;

    @JsonProperty("minor")
    public Long minor;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        VersionBuild other = (VersionBuild) o;
        if (!Objects.deepEquals(this.branch, other.branch)) return false;
        if (!Objects.deepEquals(this.buildNumber, other.buildNumber)) return false;
        if (!Objects.deepEquals(this.channel, other.channel)) return false;
        if (!Objects.deepEquals(this.commitHash, other.commitHash)) return false;
        if (!Objects.deepEquals(this.major, other.major)) return false;
        if (!Objects.deepEquals(this.minor, other.minor)) return false;

        return true;
    }
}
