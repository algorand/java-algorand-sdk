package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AccountParticipation describes the parameters used by this account in consensus 
 * protocol. 
 */
public class AccountParticipation extends PathResponse {

	/**
	 * (sel) Selection public key (if any) currently registered for this round. 
	 */	@JsonProperty("selection-participation-key")
	public Digest selectionParticipationKey;

	/**
	 * (voteFst) First round for which this participation is valid. 
	 */	@JsonProperty("vote-first-valid")
	public Long voteFirstValid;

	/**
	 * (voteKD) Number of subkeys in each batch of participation keys. 
	 */	@JsonProperty("vote-key-dilution")
	public Long voteKeyDilution;

	/**
	 * (voteLst) Last round for which this participation is valid. 
	 */	@JsonProperty("vote-last-valid")
	public Long voteLastValid;

	/**
	 * (vote) root participation public key (if any) currently registered for this 
	 * round. 
	 */	@JsonProperty("vote-participation-key")
	public Digest voteParticipationKey;

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
}
