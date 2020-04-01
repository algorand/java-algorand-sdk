package com.algorand.indexer.schemas;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	(msig) structure holding multiple subsignatures. Definition: crypto/multisig.go 
	: MultisigSig 
 */
public class TransactionSignatureMultisig {

	/*
		(thr) 
	 */
	@JsonProperty("threshold")
	public long threshold;

	/*
		(v) 
	 */
	@JsonProperty("version")
	public long version;

	/*
		(subsig) holds pairs of public key and signatures. 
	 */
	@JsonProperty("subsignature")
	public List<TransactionSignatureMultisigSubsignature> subsignature;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		TransactionSignatureMultisig other = (TransactionSignatureMultisig) o;
		if (!Objects.deepEquals(this.threshold, other.threshold)) return false;
		if (!Objects.deepEquals(this.version, other.version)) return false;
		if (!Objects.deepEquals(this.subsignature, other.subsignature)) return false;

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
