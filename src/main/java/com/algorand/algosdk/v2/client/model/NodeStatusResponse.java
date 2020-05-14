package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NodeStatusResponse extends PathResponse {

	/**
	 * CatchupTime in nanoseconds 
	 */
	@JsonProperty("catchup-time")
	public Long catchupTime;

	/**
	 * LastRound indicates the last round seen 
	 */
	@JsonProperty("last-round")
	public Long lastRound;

	/**
	 * LastVersion indicates the last consensus version supported 
	 */
	@JsonProperty("last-version")
	public String lastVersion;

	/**
	 * NextVersion of consensus protocol to use 
	 */
	@JsonProperty("next-version")
	public String nextVersion;

	/**
	 * NextVersionRound is the round at which the next consensus version will apply 
	 */
	@JsonProperty("next-version-round")
	public Long nextVersionRound;

	/**
	 * NextVersionSupported indicates whether the next consensus version is supported 
	 * by this node 
	 */
	@JsonProperty("next-version-supported")
	public Boolean nextVersionSupported;

	/**
	 * StoppedAtUnsupportedRound indicates that the node does not support the new 
	 * rounds and has stopped making progress 
	 */
	@JsonProperty("stopped-at-unsupported-round")
	public Boolean stoppedAtUnsupportedRound;

	/**
	 * TimeSinceLastRound in nanoseconds 
	 */
	@JsonProperty("time-since-last-round")
	public Long timeSinceLastRound;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		NodeStatusResponse other = (NodeStatusResponse) o;
		if (!Objects.deepEquals(this.catchupTime, other.catchupTime)) return false;
		if (!Objects.deepEquals(this.lastRound, other.lastRound)) return false;
		if (!Objects.deepEquals(this.lastVersion, other.lastVersion)) return false;
		if (!Objects.deepEquals(this.nextVersion, other.nextVersion)) return false;
		if (!Objects.deepEquals(this.nextVersionRound, other.nextVersionRound)) return false;
		if (!Objects.deepEquals(this.nextVersionSupported, other.nextVersionSupported)) return false;
		if (!Objects.deepEquals(this.stoppedAtUnsupportedRound, other.stoppedAtUnsupportedRound)) return false;
		if (!Objects.deepEquals(this.timeSinceLastRound, other.timeSinceLastRound)) return false;

		return true;
	}
}
