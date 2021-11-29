package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request data type for dryrun endpoint. Given the Transactions and simulated
 * ledger state upload, run TEAL scripts and return debugging information.
 */
public class DryrunRequest extends PathResponse {

    @JsonProperty("accounts")
    public List<Account> accounts = new ArrayList<Account>();

    @JsonProperty("apps")
    public List<Application> apps = new ArrayList<Application>();

    /**
     * LatestTimestamp is available to some TEAL scripts. Defaults to the latest
     * confirmed timestamp this algod is attached to.
     */
    @JsonProperty("latest-timestamp")
    public Long latestTimestamp;

    /**
     * ProtocolVersion specifies a specific version string to operate under, otherwise
     * whatever the current protocol of the network this algod is running in.
     */
    @JsonProperty("protocol-version")
    public String protocolVersion;

    /**
     * Round is available to some TEAL scripts. Defaults to the current round on the
     * network this algod is attached to.
     */
    @JsonProperty("round")
    public java.math.BigInteger round;

    @JsonProperty("sources")
    public List<DryrunSource> sources = new ArrayList<DryrunSource>();

    @JsonProperty("txns")
    public List<SignedTransaction> txns = new ArrayList<SignedTransaction>();

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        DryrunRequest other = (DryrunRequest) o;
        if (!Objects.deepEquals(this.accounts, other.accounts)) return false;
        if (!Objects.deepEquals(this.apps, other.apps)) return false;
        if (!Objects.deepEquals(this.latestTimestamp, other.latestTimestamp)) return false;
        if (!Objects.deepEquals(this.protocolVersion, other.protocolVersion)) return false;
        if (!Objects.deepEquals(this.round, other.round)) return false;
        if (!Objects.deepEquals(this.sources, other.sources)) return false;
        if (!Objects.deepEquals(this.txns, other.txns)) return false;

        return true;
    }
}
