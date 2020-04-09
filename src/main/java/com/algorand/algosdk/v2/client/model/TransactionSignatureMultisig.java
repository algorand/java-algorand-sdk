package com.algorand.algosdk.v2.client.model;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	(msig) structure holding multiple subsignatures. Definition: crypto/multisig.go 
	: MultisigSig 
 */
public class TransactionSignatureMultisig {

	/*
		(subsig) holds pairs of public key and signatures. 
	 */
	private List<TransactionSignatureMultisigSubsignature> subsignature;
	private boolean subsignatureIsSet;
	@JsonProperty("subsignature")
	public void setSubsignature(List<TransactionSignatureMultisigSubsignature> subsignature){
		this.subsignature = subsignature;
		subsignatureIsSet = true;
	}
	@JsonProperty("subsignature")
	public List<TransactionSignatureMultisigSubsignature> getSubsignature(){
		return subsignatureIsSet ? subsignature : null;
	}
	/*
		Check if has a value for subsignature 
	 */	@JsonIgnore
	public boolean hasSubsignature(){
		return subsignatureIsSet;
	}

	/*
		(thr) 
	 */
	private long threshold;
	private boolean thresholdIsSet;
	@JsonProperty("threshold")
	public void setThreshold(long threshold){
		this.threshold = threshold;
		thresholdIsSet = true;
	}
	@JsonProperty("threshold")
	public Long getThreshold(){
		return thresholdIsSet ? threshold : null;
	}
	/*
		Check if has a value for threshold 
	 */	@JsonIgnore
	public boolean hasThreshold(){
		return thresholdIsSet;
	}

	/*
		(v) 
	 */
	private long version;
	private boolean versionIsSet;
	@JsonProperty("version")
	public void setVersion(long version){
		this.version = version;
		versionIsSet = true;
	}
	@JsonProperty("version")
	public Long getVersion(){
		return versionIsSet ? version : null;
	}
	/*
		Check if has a value for version 
	 */	@JsonIgnore
	public boolean hasVersion(){
		return versionIsSet;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		TransactionSignatureMultisig other = (TransactionSignatureMultisig) o;
		if (!Objects.deepEquals(this.subsignature, other.subsignature)) return false;
		if (!Objects.deepEquals(this.threshold, other.threshold)) return false;
		if (!Objects.deepEquals(this.version, other.version)) return false;

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
