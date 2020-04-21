package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * (msig) structure holding multiple subsignatures. 
 * Definition: 
 * crypto/multisig.go : MultisigSig 
 */
public class TransactionSignatureMultisig extends PathResponse {

	/**
	 * (subsig) holds pairs of public key and signatures. 
	 */	@JsonProperty("subsignature")
	public List<TransactionSignatureMultisigSubsignature> subsignature = new ArrayList<TransactionSignatureMultisigSubsignature>();

	/**
	 * (thr) 
	 */	@JsonProperty("threshold")
	public Long threshold;

	/**
	 * (v) 
	 */	@JsonProperty("version")
	public Long version;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		TransactionSignatureMultisig other = (TransactionSignatureMultisig) o;
		if (!Objects.deepEquals(this.subsignature, other.subsignature)) return false;
		if (!Objects.deepEquals(this.threshold, other.threshold)) return false;
		if (!Objects.deepEquals(this.version, other.version)) return false;

		return true;
	}
}
