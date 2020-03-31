package com.algorand.indexer.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionSignatureMultisigSubsignature {

	@JsonProperty("public-key")
	public String publicKey;

	@JsonProperty("signature")
	public String signature;
}
