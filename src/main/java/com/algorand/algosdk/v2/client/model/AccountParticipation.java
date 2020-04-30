package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AccountParticipation describes the parameters used by this account in consensus 
 * protocol. 
 */
public class AccountParticipation extends PathResponse {

	/**
	 * (sel) Selection public key (if any) currently registered for this round. 
	 */ @JsonProperty("selection-participation-key")
	public void selectionParticipationKey(String base64Encoded) {
		 this.selectionParticipationKey = Encoder.decodeFromBase64(base64Encoded);
	 }
	 @JsonProperty("selection-participation-key")
	 public String selectionParticipationKey() {
		 return Encoder.encodeToBase64(this.selectionParticipationKey);
	 }
	public byte[] selectionParticipationKey;

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
	 */ @JsonProperty("vote-participation-key")
	public void voteParticipationKey(String base64Encoded) {
		 this.voteParticipationKey = Encoder.decodeFromBase64(base64Encoded);
	 }
	 @JsonProperty("vote-participation-key")
	 public String voteParticipationKey() {
		 return Encoder.encodeToBase64(this.voteParticipationKey);
	 }
	public byte[] voteParticipationKey;

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
