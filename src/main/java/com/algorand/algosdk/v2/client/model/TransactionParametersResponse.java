package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TransactionParametersResponse {

	/*
		ConsensusVersion indicates the consensus protocol version as of LastRound. 
	 */
	@JsonProperty("consensus-version")
	public String consensusVersion;

	/*
		Fee is the suggested transaction fee Fee is in units of micro-Algos per byte. 
		Fee may fall to zero but transactions must still have a fee of at least 
		MinTxnFee for the current network protocol. 
	 */
	@JsonProperty("fee")
	public long fee;

	/*
		GenesisHash is the hash of the genesis block. 
	 */
	@JsonProperty("genesis-hash")
	public String genesisHash;

	/*
		GenesisID is an ID listed in the genesis block. 
	 */
	@JsonProperty("genesis-id")
	public String genesisId;

	/*
		LastRound indicates the last round seen 
	 */
	@JsonProperty("last-round")
	public long lastRound;

	/*
		The minimum transaction fee (not per byte) required for the txn to validate for 
		the current network protocol. 
	 */
	@JsonProperty("min-fee")
	public long minFee;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		TransactionParametersResponse other = (TransactionParametersResponse) o;
		if (!Objects.deepEquals(this.consensusVersion, other.consensusVersion)) return false;
		if (!Objects.deepEquals(this.fee, other.fee)) return false;
		if (!Objects.deepEquals(this.genesisHash, other.genesisHash)) return false;
		if (!Objects.deepEquals(this.genesisId, other.genesisId)) return false;
		if (!Objects.deepEquals(this.lastRound, other.lastRound)) return false;
		if (!Objects.deepEquals(this.minFee, other.minFee)) return false;

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
