package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * algod version information.
 */
public class Version extends PathResponse {

    @JsonProperty("build")
    public BuildVersion build;

    @JsonProperty("genesis_hash_b64")
    public void genesis_hash_b64(String base64Encoded) {
        this.genesis_hash_b64 = Encoder.decodeFromBase64(base64Encoded);
    }
    public String genesis_hash_b64() {
        return Encoder.encodeToBase64(this.genesis_hash_b64);
    }
    public byte[] genesis_hash_b64;

    @JsonProperty("genesis_id")
    public String genesis_id;

    @JsonProperty("versions")
    public List<String> versions = new ArrayList<String>();

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        Version other = (Version) o;
        if (!Objects.deepEquals(this.build, other.build)) return false;
        if (!Objects.deepEquals(this.genesis_hash_b64, other.genesis_hash_b64)) return false;
        if (!Objects.deepEquals(this.genesis_id, other.genesis_id)) return false;
        if (!Objects.deepEquals(this.versions, other.versions)) return false;

        return true;
    }
}
