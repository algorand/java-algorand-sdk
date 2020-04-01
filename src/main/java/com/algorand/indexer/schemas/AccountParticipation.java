package com.algorand.indexer.schemas;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	AccountParticipation describes the parameters used by this account in consensus 
	protocol. 
 */
public class AccountParticipation {

	/*
		(sel) Selection public key (if any) currently registered for this round. 
	 */
	@JsonProperty("selection-participation-key")
	public String selectionParticipationKey;

	/*
		(voteFst) First round for which this participation is valid. 
	 */
	@JsonProperty("vote-first-valid")
	public long voteFirstValid;

	/*
		(voteKD) Number of subkeys in each batch of participation keys. 
	 */
	@JsonProperty("vote-key-dilution")
	public long voteKeyDilution;

	/*
		(voteLst) Last round for which this participation is valid. 
	 */
	@JsonProperty("vote-last-valid")
	public long voteLastValid;

	/*
		(vote) root participation public key (if any) currently registered for this 
		round. 
	 */
	@JsonProperty("vote-participation-key")
	public String voteParticipationKey;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		AccountParticipation other = (AccountParticipation) o;
		if (!Objects.deepEquals(this.selectionParticipationKey, other.selectionParticipationKey)) return false;
		if (!Objects.deepEquals(this.voteFirstValid, other.voteFirstValid)) return false;
		if (!Objects.deepEquals(this.voteKeyDilution, other.voteKeyDilution)) return false;
		if (!Objects.deepEquals(this.voteLastValid, other.voteLastValid)) return false;
		if (!Objects.deepEquals(this.voteParticipationKey, other.voteParticipationKey)) return false;

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
