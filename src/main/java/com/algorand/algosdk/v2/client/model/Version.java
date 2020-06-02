package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Note that we annotate this as a model so that legacy clients 
 * can directly import a swagger generated Version model. 
 */
public class Version extends PathResponse {

    @JsonProperty("build")
    public VersionBuild build;

    @JsonProperty("genesis-hash")
    public void genesisHash(String base64Encoded) {
        this.genesisHash = Encoder.decodeFromBase64(base64Encoded);
    }
    @JsonProperty("genesis-hash")
    public String genesisHash() {
        return Encoder.encodeToBase64(this.genesisHash);
    }
    public byte[] genesisHash;

    @JsonProperty("genesis-id")
    public String genesisId;

    @JsonProperty("versions")
    public List<String> versions = new ArrayList<String>();

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        Version other = (Version) o;
        if (!Objects.deepEquals(this.build, other.build)) return false;
        if (!Objects.deepEquals(this.genesisHash, other.genesisHash)) return false;
        if (!Objects.deepEquals(this.genesisId, other.genesisId)) return false;
        if (!Objects.deepEquals(this.versions, other.versions)) return false;

        return true;
    }
}
