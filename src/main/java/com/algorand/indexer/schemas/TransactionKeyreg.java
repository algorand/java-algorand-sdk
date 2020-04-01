package com.algorand.indexer.schemas;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	Fields for a keyreg transaction. Definition: data/transactions/keyreg.go : 
	KeyregTxnFields 
 */
public class TransactionKeyreg {

	/*
		(votekey) Participation public key used in key registration transactions. 
	 */
	@JsonProperty("vote-participation-key")
	public String voteParticipationKey;

	/*
		(votekd) Number of subkeys in each batch of participation keys. 
	 */
	@JsonProperty("vote-key-dilution")
	public long voteKeyDilution;

	/*
		(selkey) Public key used with the Verified Random Function (VRF) result during 
		committee selection. 
	 */
	@JsonProperty("selection-participation-key")
	public String selectionParticipationKey;

	/*
		(votefst) First round this participation key is valid. 
	 */
	@JsonProperty("vote-first-valid")
	public long voteFirstValid;

	/*
		(nonpart) Mark the account as participating or non-participating. 
	 */
	@JsonProperty("non-participation")
	public boolean nonParticipation;

	/*
		(votelst) Last round this participation key is valid. 
	 */
	@JsonProperty("vote-last-valid")
	public long voteLastValid;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		TransactionKeyreg other = (TransactionKeyreg) o;
		if (!Objects.deepEquals(this.voteParticipationKey, other.voteParticipationKey)) return false;
		if (!Objects.deepEquals(this.voteKeyDilution, other.voteKeyDilution)) return false;
		if (!Objects.deepEquals(this.selectionParticipationKey, other.selectionParticipationKey)) return false;
		if (!Objects.deepEquals(this.voteFirstValid, other.voteFirstValid)) return false;
		if (!Objects.deepEquals(this.nonParticipation, other.nonParticipation)) return false;
		if (!Objects.deepEquals(this.voteLastValid, other.voteLastValid)) return false;

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
