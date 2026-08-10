package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The status of a connected peer in the P2P network
 */
public class PeerStatus extends PathResponse {

    /**
     * Connection type
     */
    @JsonProperty("connection-type")
    public Enums.ConnectionType connectionType;

    /**
     * Network address of the peer
     */
    @JsonProperty("network-address")
    public String networkAddress;

    /**
     * Network type
     */
    @JsonProperty("network-type")
    public Enums.NetworkType networkType;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        PeerStatus other = (PeerStatus) o;
        if (!Objects.deepEquals(this.connectionType, other.connectionType)) return false;
        if (!Objects.deepEquals(this.networkAddress, other.networkAddress)) return false;
        if (!Objects.deepEquals(this.networkType, other.networkType)) return false;

        return true;
    }
}
