package com.algorand.indexer.schemas;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TransactionSignatureMultisigSubsignature {

	/*
		(pk) 
	 */
	@JsonProperty("public-key")
	public String publicKey;

	/*
		(s) 
	 */
	@JsonProperty("signature")
	public String signature;

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
			jsonStr = om.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
		}
		return jsonStr;
	}
}
