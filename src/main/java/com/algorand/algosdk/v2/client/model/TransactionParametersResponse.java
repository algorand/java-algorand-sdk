package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TransactionParametersResponse {

	/**
	 * ConsensusVersion indicates the consensus protocol version as of LastRound. 
	 */
	private String consensusVersion;
	private boolean consensusVersionIsSet;
	@JsonProperty("consensus-version")
	public void setConsensusVersion(String consensusVersion){
		this.consensusVersion = consensusVersion;
		consensusVersionIsSet = true;
	}
	@JsonProperty("consensus-version")
	public String getConsensusVersion(){
		return consensusVersionIsSet ? consensusVersion : null;
	}
	/**
	 * Check if has a value for consensusVersion 
	 */	@JsonIgnore
	public boolean hasConsensusVersion(){
		return consensusVersionIsSet;
	}

	/**
	 * Fee is the suggested transaction fee Fee is in units of micro-Algos per byte. 
	 * Fee may fall to zero but transactions must still have a fee of at least 
	 * MinTxnFee for the current network protocol. 
	 */
	private long fee;
	private boolean feeIsSet;
	@JsonProperty("fee")
	public void setFee(long fee){
		this.fee = fee;
		feeIsSet = true;
	}
	@JsonProperty("fee")
	public Long getFee(){
		return feeIsSet ? fee : null;
	}
	/**
	 * Check if has a value for fee 
	 */	@JsonIgnore
	public boolean hasFee(){
		return feeIsSet;
	}

	/**
	 * GenesisHash is the hash of the genesis block. 
	 */
	private String genesisHash;
	private boolean genesisHashIsSet;
	@JsonProperty("genesis-hash")
	public void setGenesisHash(String genesisHash){
		this.genesisHash = genesisHash;
		genesisHashIsSet = true;
	}
	@JsonProperty("genesis-hash")
	public String getGenesisHash(){
		return genesisHashIsSet ? genesisHash : null;
	}
	/**
	 * Check if has a value for genesisHash 
	 */	@JsonIgnore
	public boolean hasGenesisHash(){
		return genesisHashIsSet;
	}

	/**
	 * GenesisID is an ID listed in the genesis block. 
	 */
	private String genesisId;
	private boolean genesisIdIsSet;
	@JsonProperty("genesis-id")
	public void setGenesisId(String genesisId){
		this.genesisId = genesisId;
		genesisIdIsSet = true;
	}
	@JsonProperty("genesis-id")
	public String getGenesisId(){
		return genesisIdIsSet ? genesisId : null;
	}
	/**
	 * Check if has a value for genesisId 
	 */	@JsonIgnore
	public boolean hasGenesisId(){
		return genesisIdIsSet;
	}

	/**
	 * LastRound indicates the last round seen 
	 */
	private long lastRound;
	private boolean lastRoundIsSet;
	@JsonProperty("last-round")
	public void setLastRound(long lastRound){
		this.lastRound = lastRound;
		lastRoundIsSet = true;
	}
	@JsonProperty("last-round")
	public Long getLastRound(){
		return lastRoundIsSet ? lastRound : null;
	}
	/**
	 * Check if has a value for lastRound 
	 */	@JsonIgnore
	public boolean hasLastRound(){
		return lastRoundIsSet;
	}

	/**
	 * The minimum transaction fee (not per byte) required for the txn to validate for 
	 * the current network protocol. 
	 */
	private long minFee;
	private boolean minFeeIsSet;
	@JsonProperty("min-fee")
	public void setMinFee(long minFee){
		this.minFee = minFee;
		minFeeIsSet = true;
	}
	@JsonProperty("min-fee")
	public Long getMinFee(){
		return minFeeIsSet ? minFee : null;
	}
	/**
	 * Check if has a value for minFee 
	 */	@JsonIgnore
	public boolean hasMinFee(){
		return minFeeIsSet;
	}

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
			jsonStr = om.setSerializationInclusion(Include.NON_NULL).writeValueAsString(this);

		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
		}
		return jsonStr;
	}
}
