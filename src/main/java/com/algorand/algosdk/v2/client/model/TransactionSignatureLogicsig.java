package com.algorand.algosdk.v2.client.model;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	(lsig) Programatic transaction signature. Definition: 
	data/transactions/logicsig.go 
 */
public class TransactionSignatureLogicsig {

	/*
		(arg) Logic arguments, base64 encoded. 
	 */
	private List<String> args;
	private boolean argsIsSet;
	@JsonProperty("args")
	public void setArgs(List<String> args){
		this.args = args;
		argsIsSet = true;
	}
	@JsonProperty("args")
	public List<String> getArgs(){
		return argsIsSet ? args : null;
	}
	/*
		Check if has a value for args 
	 */	@JsonIgnore
	public boolean hasArgs(){
		return argsIsSet;
	}

	/*
		(l) Program signed by a signature or multi signature, or hashed to be the 
		address of ana ccount. Base64 encoded TEAL program. 
	 */
	private String logic;
	private boolean logicIsSet;
	@JsonProperty("logic")
	public void setLogic(String logic){
		this.logic = logic;
		logicIsSet = true;
	}
	@JsonProperty("logic")
	public String getLogic(){
		return logicIsSet ? logic : null;
	}
	/*
		Check if has a value for logic 
	 */	@JsonIgnore
	public boolean hasLogic(){
		return logicIsSet;
	}

	private TransactionSignatureMultisig multisigSignature;
	private boolean multisigSignatureIsSet;
	@JsonProperty("multisig-signature")
	public void setMultisigSignature(TransactionSignatureMultisig multisigSignature){
		this.multisigSignature = multisigSignature;
		multisigSignatureIsSet = true;
	}
	@JsonProperty("multisig-signature")
	public TransactionSignatureMultisig getMultisigSignature(){
		return multisigSignatureIsSet ? multisigSignature : null;
	}
	/*
		Check if has a value for multisigSignature 
	 */	@JsonIgnore
	public boolean hasMultisigSignature(){
		return multisigSignatureIsSet;
	}

	/*
		(sig) ed25519 signature. 
	 */
	private String signature;
	private boolean signatureIsSet;
	@JsonProperty("signature")
	public void setSignature(String signature){
		this.signature = signature;
		signatureIsSet = true;
	}
	@JsonProperty("signature")
	public String getSignature(){
		return signatureIsSet ? signature : null;
	}
	/*
		Check if has a value for signature 
	 */	@JsonIgnore
	public boolean hasSignature(){
		return signatureIsSet;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		TransactionSignatureLogicsig other = (TransactionSignatureLogicsig) o;
		if (!Objects.deepEquals(this.args, other.args)) return false;
		if (!Objects.deepEquals(this.logic, other.logic)) return false;
		if (!Objects.deepEquals(this.multisigSignature, other.multisigSignature)) return false;
		if (!Objects.deepEquals(this.signature, other.signature)) return false;

		return true;
	}

	@Override
	public String toString() {
		ObjectMapper om = new ObjectMapper(); 
		String jsonStr;
		try {
			jsonStr = om.setSerializationInclusion(Include.NON_NULL).writeValueAsString(this);

		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
		}
		return jsonStr;
	}
}
