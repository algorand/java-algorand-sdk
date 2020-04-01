package com.algorand.indexer.schemas;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	Validation signature associated with some data. Only one of the signatures 
	should be provided. 
 */
public class TransactionSignature {

	@JsonProperty("logicsig")
	public TransactionSignatureLogicsig logicsig;

	@JsonProperty("multisig")
	public TransactionSignatureMultisig multisig;

	/*
		(sig) Standard ed25519 signature. 
	 */
	@JsonProperty("sig")
	public String sig;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		TransactionSignature other = (TransactionSignature) o;
		if (!Objects.deepEquals(this.logicsig, other.logicsig)) return false;
		if (!Objects.deepEquals(this.multisig, other.multisig)) return false;
		if (!Objects.deepEquals(this.sig, other.sig)) return false;

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
