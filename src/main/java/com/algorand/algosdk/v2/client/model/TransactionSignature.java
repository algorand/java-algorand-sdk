package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Validation signature associated with some data. Only one of the signatures 
 * should be provided. 
 */
public class TransactionSignature extends PathResponse {

	private TransactionSignatureLogicsig logicsig;
	private boolean logicsigIsSet;
	@JsonProperty("logicsig")
	public void setLogicsig(TransactionSignatureLogicsig logicsig){
		this.logicsig = logicsig;
		logicsigIsSet = true;
	}
	@JsonProperty("logicsig")
	public TransactionSignatureLogicsig getLogicsig(){
		return logicsigIsSet ? logicsig : null;
	}
	/**
	 * Check if has a value for logicsig 
	 */	@JsonIgnore
	public boolean hasLogicsig(){
		return logicsigIsSet;
	}

	private TransactionSignatureMultisig multisig;
	private boolean multisigIsSet;
	@JsonProperty("multisig")
	public void setMultisig(TransactionSignatureMultisig multisig){
		this.multisig = multisig;
		multisigIsSet = true;
	}
	@JsonProperty("multisig")
	public TransactionSignatureMultisig getMultisig(){
		return multisigIsSet ? multisig : null;
	}
	/**
	 * Check if has a value for multisig 
	 */	@JsonIgnore
	public boolean hasMultisig(){
		return multisigIsSet;
	}

	/**
	 * (sig) Standard ed25519 signature. 
	 */
	private String sig;
	private boolean sigIsSet;
	@JsonProperty("sig")
	public void setSig(String sig){
		this.sig = sig;
		sigIsSet = true;
	}
	@JsonProperty("sig")
	public String getSig(){
		return sigIsSet ? sig : null;
	}
	/**
	 * Check if has a value for sig 
	 */	@JsonIgnore
	public boolean hasSig(){
		return sigIsSet;
	}

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
}
