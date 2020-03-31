package com.algorand.indexer.schemas;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionSignatureMultisig {
	@JsonProperty("threshold")
	public long threshold;
	
	@JsonProperty("version")
	public long version;
	
	@JsonProperty("subsignature")
	public List<TransactionSignatureMultisigSubsignature> subsignature;
}
