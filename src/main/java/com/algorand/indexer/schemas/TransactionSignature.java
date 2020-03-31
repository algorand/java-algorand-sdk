package com.algorand.indexer.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionSignature {

	@JsonProperty("multisig")
	public TransactionSignatureMultisig multisig;

	@JsonProperty("logicsig")
	public TransactionSignatureLogicsig logicsig;

	@JsonProperty("sig")
	public String sig;
}
