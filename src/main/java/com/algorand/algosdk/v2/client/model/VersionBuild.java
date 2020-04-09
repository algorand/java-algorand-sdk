package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * the current algod build version information. 
 */
public class VersionBuild {

	private String branch;
	private boolean branchIsSet;
	@JsonProperty("branch")
	public void setBranch(String branch){
		this.branch = branch;
		branchIsSet = true;
	}
	@JsonProperty("branch")
	public String getBranch(){
		return branchIsSet ? branch : null;
	}
	/**
	 * Check if has a value for branch 
	 */	@JsonIgnore
	public boolean hasBranch(){
		return branchIsSet;
	}

	private long buildNumber;
	private boolean buildNumberIsSet;
	@JsonProperty("build-number")
	public void setBuildNumber(long buildNumber){
		this.buildNumber = buildNumber;
		buildNumberIsSet = true;
	}
	@JsonProperty("build-number")
	public Long getBuildNumber(){
		return buildNumberIsSet ? buildNumber : null;
	}
	/**
	 * Check if has a value for buildNumber 
	 */	@JsonIgnore
	public boolean hasBuildNumber(){
		return buildNumberIsSet;
	}

	private String channel;
	private boolean channelIsSet;
	@JsonProperty("channel")
	public void setChannel(String channel){
		this.channel = channel;
		channelIsSet = true;
	}
	@JsonProperty("channel")
	public String getChannel(){
		return channelIsSet ? channel : null;
	}
	/**
	 * Check if has a value for channel 
	 */	@JsonIgnore
	public boolean hasChannel(){
		return channelIsSet;
	}

	private String commitHash;
	private boolean commitHashIsSet;
	@JsonProperty("commit-hash")
	public void setCommitHash(String commitHash){
		this.commitHash = commitHash;
		commitHashIsSet = true;
	}
	@JsonProperty("commit-hash")
	public String getCommitHash(){
		return commitHashIsSet ? commitHash : null;
	}
	/**
	 * Check if has a value for commitHash 
	 */	@JsonIgnore
	public boolean hasCommitHash(){
		return commitHashIsSet;
	}

	private long major;
	private boolean majorIsSet;
	@JsonProperty("major")
	public void setMajor(long major){
		this.major = major;
		majorIsSet = true;
	}
	@JsonProperty("major")
	public Long getMajor(){
		return majorIsSet ? major : null;
	}
	/**
	 * Check if has a value for major 
	 */	@JsonIgnore
	public boolean hasMajor(){
		return majorIsSet;
	}

	private long minor;
	private boolean minorIsSet;
	@JsonProperty("minor")
	public void setMinor(long minor){
		this.minor = minor;
		minorIsSet = true;
	}
	@JsonProperty("minor")
	public Long getMinor(){
		return minorIsSet ? minor : null;
	}
	/**
	 * Check if has a value for minor 
	 */	@JsonIgnore
	public boolean hasMinor(){
		return minorIsSet;
	}

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
			jsonStr = om.setSerializationInclusion(Include.NON_NULL).writeValueAsString(this);

		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
		}
		return jsonStr;
	}
}
