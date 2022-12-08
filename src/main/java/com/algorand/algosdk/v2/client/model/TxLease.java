package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TxLease extends PathResponse {

    /**
     * Round that the lease expires
     */
    @JsonProperty("expiration")
    public Long expiration;

    /**
     * Lease data
     */
    @JsonProperty("lease")
    public void lease(String base64Encoded) {
        this.lease = Encoder.decodeFromBase64(base64Encoded);
    }
    public String lease() {
        return Encoder.encodeToBase64(this.lease);
    }
    public byte[] lease;

    /**
     * Address of the lease sender
     */
    @JsonProperty("sender")
    public String sender;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        TxLease other = (TxLease) o;
        if (!Objects.deepEquals(this.expiration, other.expiration)) return false;
        if (!Objects.deepEquals(this.lease, other.lease)) return false;
        if (!Objects.deepEquals(this.sender, other.sender)) return false;

        return true;
    }
}
