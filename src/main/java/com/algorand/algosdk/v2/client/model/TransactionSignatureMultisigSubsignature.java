package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TransactionSignatureMultisigSubsignature {

	/*
		(pk) 
	 */
	private String publicKey;
	private boolean publicKeyIsSet;
	@JsonProperty("public-key")
	public void setPublicKey(String publicKey){
		this.publicKey = publicKey;
		publicKeyIsSet = true;
	}
	@JsonProperty("public-key")
	public String getPublicKey(){
		return publicKeyIsSet ? publicKey : null;
	}
	/*
		Check if has a value for publicKey 
	 */	@JsonIgnore
	public boolean hasPublicKey(){
		return publicKeyIsSet;
	}

	/*
		(s) 
	 */
	private String signature;
	private boolean signatureIsSet;
	@JsonProperty("signature")
	public void setSignature(String signature){
		this.signature = signature;
		signatureIsSet = true;
	}
	@JsonProperty("signature")
	public String getSignature(){
		return signatureIsSet ? signature : null;
	}
	/*
		Check if has a value for signature 
	 */	@JsonIgnore
	public boolean hasSignature(){
		return signatureIsSet;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		TransactionSignatureMultisigSubsignature other = (TransactionSignatureMultisigSubsignature) o;
		if (!Objects.deepEquals(this.publicKey, other.publicKey)) return false;
		if (!Objects.deepEquals(this.signature, other.signature)) return false;

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
