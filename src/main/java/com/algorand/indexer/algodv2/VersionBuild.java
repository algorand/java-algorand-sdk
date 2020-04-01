package com.algorand.indexer.algodv2;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	the current algod build version information. 
 */
public class VersionBuild {

	@JsonProperty("branch")
	public String branch;

	@JsonProperty("build-number")
	public long buildNumber;

	@JsonProperty("channel")
	public String channel;

	@JsonProperty("commit-hash")
	public String commitHash;

	@JsonProperty("major")
	public long major;

	@JsonProperty("minor")
	public long minor;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		VersionBuild other = (VersionBuild) o;
		if (!Objects.deepEquals(this.branch, other.branch)) return false;
		if (!Objects.deepEquals(this.buildNumber, other.buildNumber)) return false;
		if (!Objects.deepEquals(this.channel, other.channel)) return false;
		if (!Objects.deepEquals(this.commitHash, other.commitHash)) return false;
		if (!Objects.deepEquals(this.major, other.major)) return false;
		if (!Objects.deepEquals(this.minor, other.minor)) return false;

		return true;
	}

	@Override
	public String toString() {
		ObjectMapper om = new ObjectMapper(); 
		String jsonStr;
		try {
			jsonStr = om.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
		}
		return jsonStr;
	}
}
