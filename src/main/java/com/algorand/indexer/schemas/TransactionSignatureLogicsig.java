package com.algorand.indexer.schemas;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionSignatureLogicsig {

	@JsonProperty("logic")
	public String logic;

	@JsonProperty("args")
	public List<String> args;

	@JsonProperty("multisig-signature")
	public TransactionSignatureMultisig multisigSignature;

	@JsonProperty("signature")
	public String signature;
}
