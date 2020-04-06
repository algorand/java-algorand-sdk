package com.algorand.algosdk.v2.client.model;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	Note that we annotate this as a model so that legacy clients can directly import 
	a swagger generated Version model. 
 */
public class Version {

	@JsonProperty("build")
	public VersionBuild build;

	@JsonProperty("genesis-hash")
	public String genesisHash;

	@JsonProperty("genesis-id")
	public String genesisId;

	@JsonProperty("versions")
	public List<String> versions;

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
