package com.algorand.algosdk.v2.client.model;

import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Note that we annotate this as a model so that legacy clients can directly import 
 * a swagger generated Version model. 
 */
public class Version extends PathResponse {

	private VersionBuild build;
	private boolean buildIsSet;
	@JsonProperty("build")
	public void setBuild(VersionBuild build){
		this.build = build;
		buildIsSet = true;
	}
	@JsonProperty("build")
	public VersionBuild getBuild(){
		return buildIsSet ? build : null;
	}
	/**
	 * Check if has a value for build 
	 */	@JsonIgnore
	public boolean hasBuild(){
		return buildIsSet;
	}

	private String genesisHash;
	private boolean genesisHashIsSet;
	@JsonProperty("genesis-hash")
	public void setGenesisHash(String genesisHash){
		this.genesisHash = genesisHash;
		genesisHashIsSet = true;
	}
	@JsonProperty("genesis-hash")
	public String getGenesisHash(){
		return genesisHashIsSet ? genesisHash : null;
	}
	/**
	 * Check if has a value for genesisHash 
	 */	@JsonIgnore
	public boolean hasGenesisHash(){
		return genesisHashIsSet;
	}

	private String genesisId;
	private boolean genesisIdIsSet;
	@JsonProperty("genesis-id")
	public void setGenesisId(String genesisId){
		this.genesisId = genesisId;
		genesisIdIsSet = true;
	}
	@JsonProperty("genesis-id")
	public String getGenesisId(){
		return genesisIdIsSet ? genesisId : null;
	}
	/**
	 * Check if has a value for genesisId 
	 */	@JsonIgnore
	public boolean hasGenesisId(){
		return genesisIdIsSet;
	}

	private List<String> versions;
	private boolean versionsIsSet;
	@JsonProperty("versions")
	public void setVersions(List<String> versions){
		this.versions = versions;
		versionsIsSet = true;
	}
	@JsonProperty("versions")
	public List<String> getVersions(){
		return versionsIsSet ? versions : null;
	}
	/**
	 * Check if has a value for versions 
	 */	@JsonIgnore
	public boolean hasVersions(){
		return versionsIsSet;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		Version other = (Version) o;
		if (!Objects.deepEquals(this.build, other.build)) return false;
		if (!Objects.deepEquals(this.genesisHash, other.genesisHash)) return false;
		if (!Objects.deepEquals(this.genesisId, other.genesisId)) return false;
		if (!Objects.deepEquals(this.versions, other.versions)) return false;

		return true;
	}
}
