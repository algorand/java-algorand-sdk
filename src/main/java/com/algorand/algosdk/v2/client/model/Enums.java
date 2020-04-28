package com.algorand.algosdk.v2.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Enums {

/**
 * Combine with the address parameter to define what type of address to search for. 
 */
	public enum AddressRole {
		@JsonProperty("sender") SENDER,
		@JsonProperty("receiver") RECEIVER,
		@JsonProperty("freeze-target") FREEZETARGET
	}

/**
 * Configures whether the response object is JSON or MessagePack encoded. 
 */
	public enum Format {
		@JsonProperty("json") JSON,
		@JsonProperty("msgpack") MSGPACK
	}

/**
 * SigType filters just results using the specified type of signature: 
 *   sig - Standard 
 *   msig - MultiSig 
 *   lsig - LogicSig 
 */
	public enum SigType {
		@JsonProperty("sig") SIG,
		@JsonProperty("msig") MSIG,
		@JsonProperty("lsig") LSIG
	}

	public enum TxType {
		@JsonProperty("pay") PAY,
		@JsonProperty("keyreg") KEYREG,
		@JsonProperty("acfg") ACFG,
		@JsonProperty("axfer") AXFER,
		@JsonProperty("afrz") AFRZ
	}

}
