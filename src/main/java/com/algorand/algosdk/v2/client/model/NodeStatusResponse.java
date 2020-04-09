package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NodeStatusResponse {

	/**
	 * CatchupTime in nanoseconds 
	 */
	private long catchupTime;
	private boolean catchupTimeIsSet;
	@JsonProperty("catchup-time")
	public void setCatchupTime(long catchupTime){
		this.catchupTime = catchupTime;
		catchupTimeIsSet = true;
	}
	@JsonProperty("catchup-time")
	public Long getCatchupTime(){
		return catchupTimeIsSet ? catchupTime : null;
	}
	/**
	 * Check if has a value for catchupTime 
	 */	@JsonIgnore
	public boolean hasCatchupTime(){
		return catchupTimeIsSet;
	}

	/**
	 * LastRound indicates the last round seen 
	 */
	private long lastRound;
	private boolean lastRoundIsSet;
	@JsonProperty("last-round")
	public void setLastRound(long lastRound){
		this.lastRound = lastRound;
		lastRoundIsSet = true;
	}
	@JsonProperty("last-round")
	public Long getLastRound(){
		return lastRoundIsSet ? lastRound : null;
	}
	/**
	 * Check if has a value for lastRound 
	 */	@JsonIgnore
	public boolean hasLastRound(){
		return lastRoundIsSet;
	}

	/**
	 * LastVersion indicates the last consensus version supported 
	 */
	private String lastVersion;
	private boolean lastVersionIsSet;
	@JsonProperty("last-version")
	public void setLastVersion(String lastVersion){
		this.lastVersion = lastVersion;
		lastVersionIsSet = true;
	}
	@JsonProperty("last-version")
	public String getLastVersion(){
		return lastVersionIsSet ? lastVersion : null;
	}
	/**
	 * Check if has a value for lastVersion 
	 */	@JsonIgnore
	public boolean hasLastVersion(){
		return lastVersionIsSet;
	}

	/**
	 * NextVersion of consensus protocol to use 
	 */
	private String nextVersion;
	private boolean nextVersionIsSet;
	@JsonProperty("next-version")
	public void setNextVersion(String nextVersion){
		this.nextVersion = nextVersion;
		nextVersionIsSet = true;
	}
	@JsonProperty("next-version")
	public String getNextVersion(){
		return nextVersionIsSet ? nextVersion : null;
	}
	/**
	 * Check if has a value for nextVersion 
	 */	@JsonIgnore
	public boolean hasNextVersion(){
		return nextVersionIsSet;
	}

	/**
	 * NextVersionRound is the round at which the next consensus version will apply 
	 */
	private long nextVersionRound;
	private boolean nextVersionRoundIsSet;
	@JsonProperty("next-version-round")
	public void setNextVersionRound(long nextVersionRound){
		this.nextVersionRound = nextVersionRound;
		nextVersionRoundIsSet = true;
	}
	@JsonProperty("next-version-round")
	public Long getNextVersionRound(){
		return nextVersionRoundIsSet ? nextVersionRound : null;
	}
	/**
	 * Check if has a value for nextVersionRound 
	 */	@JsonIgnore
	public boolean hasNextVersionRound(){
		return nextVersionRoundIsSet;
	}

	/**
	 * NextVersionSupported indicates whether the next consensus version is supported 
	 * by this node 
	 */
	private boolean nextVersionSupported;
	private boolean nextVersionSupportedIsSet;
	@JsonProperty("next-version-supported")
	public void setNextVersionSupported(boolean nextVersionSupported){
		this.nextVersionSupported = nextVersionSupported;
		nextVersionSupportedIsSet = true;
	}
	@JsonProperty("next-version-supported")
	public Boolean getNextVersionSupported(){
		return nextVersionSupportedIsSet ? nextVersionSupported : null;
	}
	/**
	 * Check if has a value for nextVersionSupported 
	 */	@JsonIgnore
	public boolean hasNextVersionSupported(){
		return nextVersionSupportedIsSet;
	}

	/**
	 * StoppedAtUnsupportedRound indicates that the node does not support the new 
	 * rounds and has stopped making progress 
	 */
	private boolean stoppedAtUnsupportedRound;
	private boolean stoppedAtUnsupportedRoundIsSet;
	@JsonProperty("stopped-at-unsupported-round")
	public void setStoppedAtUnsupportedRound(boolean stoppedAtUnsupportedRound){
		this.stoppedAtUnsupportedRound = stoppedAtUnsupportedRound;
		stoppedAtUnsupportedRoundIsSet = true;
	}
	@JsonProperty("stopped-at-unsupported-round")
	public Boolean getStoppedAtUnsupportedRound(){
		return stoppedAtUnsupportedRoundIsSet ? stoppedAtUnsupportedRound : null;
	}
	/**
	 * Check if has a value for stoppedAtUnsupportedRound 
	 */	@JsonIgnore
	public boolean hasStoppedAtUnsupportedRound(){
		return stoppedAtUnsupportedRoundIsSet;
	}

	/**
	 * TimeSinceLastRound in nanoseconds 
	 */
	private long timeSinceLastRound;
	private boolean timeSinceLastRoundIsSet;
	@JsonProperty("time-since-last-round")
	public void setTimeSinceLastRound(long timeSinceLastRound){
		this.timeSinceLastRound = timeSinceLastRound;
		timeSinceLastRoundIsSet = true;
	}
	@JsonProperty("time-since-last-round")
	public Long getTimeSinceLastRound(){
		return timeSinceLastRoundIsSet ? timeSinceLastRound : null;
	}
	/**
	 * Check if has a value for timeSinceLastRound 
	 */	@JsonIgnore
	public boolean hasTimeSinceLastRound(){
		return timeSinceLastRoundIsSet;
	}

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

	@Override
	public String toString() {
		ObjectMapper om = new ObjectMapper(); 
		String jsonStr;
		try {
			jsonStr = om.setSerializationInclusion(Include.NON_NULL).writeValueAsString(this);

		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
		}
		return jsonStr;
	}
}
