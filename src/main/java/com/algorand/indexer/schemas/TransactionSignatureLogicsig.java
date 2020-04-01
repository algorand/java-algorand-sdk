package com.algorand.indexer.schemas;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	(l) Program signed by a signature or multi signature, or hashed to be the 
	address of ana ccount. Base64 encoded TEAL program. 
 */
public class TransactionSignatureLogicsig {

	/*
		(l) Program signed by a signature or multi signature, or hashed to be the 
		address of ana ccount. Base64 encoded TEAL program. 
	 */
	@JsonProperty("logic")
	public String logic;

	/*
		(arg) Logic arguments, base64 encoded. 
	 */
	@JsonProperty("args")
	public List<String> args;

	@JsonProperty("multisig-signature")
	public TransactionSignatureMultisig multisigSignature;

	/*
		(sig) ed25519 signature. 
	 */
	@JsonProperty("signature")
	public String signature;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		TransactionSignatureLogicsig other = (TransactionSignatureLogicsig) o;
		if (!Objects.deepEquals(this.logic, other.logic)) return false;
		if (!Objects.deepEquals(this.args, other.args)) return false;
		if (!Objects.deepEquals(this.multisigSignature, other.multisigSignature)) return false;
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
