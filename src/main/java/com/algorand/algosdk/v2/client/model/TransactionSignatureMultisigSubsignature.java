package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionSignatureMultisigSubsignature extends PathResponse {

	/**
	 * (pk) 
	 */	@JsonProperty("public-key")
	public byte[] publicKey;

	/**
	 * (s) 
	 */	@JsonProperty("signature")
	public byte[] signature;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		TransactionSignatureMultisigSubsignature other = (TransactionSignatureMultisigSubsignature) o;
		if (!Objects.deepEquals(this.publicKey, other.publicKey)) return false;
		if (!Objects.deepEquals(this.signature, other.signature)) return false;

		return true;
	}
}
