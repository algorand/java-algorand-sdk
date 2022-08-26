package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MerkleArrayProof extends PathResponse {

    @JsonProperty("hash-factory")
    public HashFactory hashFactory;

    /**
     * (pth)
     */
    @JsonProperty("path")
    public List<byte[]> path = new ArrayList<byte[]>();
    @JsonIgnore
    public void path(List<String> base64Encoded) {
        this.path = new ArrayList<byte[]>();
        for (String val : base64Encoded) {
            this.path.add(Encoder.decodeFromBase64(val));
        }
    }
    @JsonIgnore
    public List<String> path() {
        ArrayList<String> ret = new ArrayList<String>();
        for (byte[] val : this.path) {
            ret.add(Encoder.encodeToBase64(val));
        }
        return ret; 
    }

    /**
     * (td)
     */
    @JsonProperty("tree-depth")
    public Long treeDepth;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        MerkleArrayProof other = (MerkleArrayProof) o;
        if (!Objects.deepEquals(this.hashFactory, other.hashFactory)) return false;
        if (!Objects.deepEquals(this.path, other.path)) return false;
        if (!Objects.deepEquals(this.treeDepth, other.treeDepth)) return false;

        return true;
    }
}
