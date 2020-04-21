package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Fields for a keyreg transaction. 
 * Definition: 
 * data/transactions/keyreg.go : KeyregTxnFields 
 */
public class TransactionKeyreg extends PathResponse {

	/**
	 * (nonpart) Mark the account as participating or non-participating. 
	 */	@JsonProperty("non-participation")
	public Boolean nonParticipation;

	/**
	 * (selkey) Public key used with the Verified Random Function (VRF) result during 
	 * committee selection. 
	 */	@JsonProperty("selection-participation-key")
	public String selectionParticipationKey;

	/**
	 * (votefst) First round this participation key is valid. 
	 */	@JsonProperty("vote-first-valid")
	public Long voteFirstValid;

	/**
	 * (votekd) Number of subkeys in each batch of participation keys. 
	 */	@JsonProperty("vote-key-dilution")
	public Long voteKeyDilution;

	/**
	 * (votelst) Last round this participation key is valid. 
	 */	@JsonProperty("vote-last-valid")
	public Long voteLastValid;

	/**
	 * (votekey) Participation public key used in key registration transactions. 
	 */	@JsonProperty("vote-participation-key")
	public String voteParticipationKey;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		TransactionKeyreg other = (TransactionKeyreg) o;
		if (!Objects.deepEquals(this.nonParticipation, other.nonParticipation)) return false;
		if (!Objects.deepEquals(this.selectionParticipationKey, other.selectionParticipationKey)) return false;
		if (!Objects.deepEquals(this.voteFirstValid, other.voteFirstValid)) return false;
		if (!Objects.deepEquals(this.voteKeyDilution, other.voteKeyDilution)) return false;
		if (!Objects.deepEquals(this.voteLastValid, other.voteLastValid)) return false;
		if (!Objects.deepEquals(this.voteParticipationKey, other.voteParticipationKey)) return false;

		return true;
	}
}
