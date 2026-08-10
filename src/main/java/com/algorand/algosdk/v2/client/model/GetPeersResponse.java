package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response containing the network peers of the node
 */
public class GetPeersResponse extends PathResponse {

    @JsonProperty("Peers")
    public List<PeerStatus> peers = new ArrayList<PeerStatus>();

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        GetPeersResponse other = (GetPeersResponse) o;
        if (!Objects.deepEquals(this.peers, other.peers)) return false;

        return true;
    }
}
