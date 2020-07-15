package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DryrunResponse contains per-txn debug information from a dryrun.
 */
public class DryrunResponse extends PathResponse {

    @JsonProperty("error")
    public String error;

    /**
     * Protocol version is the protocol version Dryrun was operated under.
     */
    @JsonProperty("protocol-version")
    public String protocolVersion;

    @JsonProperty("txns")
    public List<DryrunTxnResult> txns = new ArrayList<DryrunTxnResult>();

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        DryrunResponse other = (DryrunResponse) o;
        if (!Objects.deepEquals(this.error, other.error)) return false;
        if (!Objects.deepEquals(this.protocolVersion, other.protocolVersion)) return false;
        if (!Objects.deepEquals(this.txns, other.txns)) return false;

        return true;
    }
}
