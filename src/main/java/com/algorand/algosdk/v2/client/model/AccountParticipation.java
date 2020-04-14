package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * AccountParticipation describes the parameters used by this account in consensus 
 * protocol. 
 */
public class AccountParticipation extends PathResponse {

	/**
	 * (sel) Selection public key (if any) currently registered for this round. 
	 */
	private String selectionParticipationKey;
	private boolean selectionParticipationKeyIsSet;
	@JsonProperty("selection-participation-key")
	public void setSelectionParticipationKey(String selectionParticipationKey){
		this.selectionParticipationKey = selectionParticipationKey;
		selectionParticipationKeyIsSet = true;
	}
	@JsonProperty("selection-participation-key")
	public String getSelectionParticipationKey(){
		return selectionParticipationKeyIsSet ? selectionParticipationKey : null;
	}
	/**
	 * Check if has a value for selectionParticipationKey 
	 */	@JsonIgnore
	public boolean hasSelectionParticipationKey(){
		return selectionParticipationKeyIsSet;
	}

	/**
	 * (voteFst) First round for which this participation is valid. 
	 */
	private long voteFirstValid;
	private boolean voteFirstValidIsSet;
	@JsonProperty("vote-first-valid")
	public void setVoteFirstValid(long voteFirstValid){
		this.voteFirstValid = voteFirstValid;
		voteFirstValidIsSet = true;
	}
	@JsonProperty("vote-first-valid")
	public Long getVoteFirstValid(){
		return voteFirstValidIsSet ? voteFirstValid : null;
	}
	/**
	 * Check if has a value for voteFirstValid 
	 */	@JsonIgnore
	public boolean hasVoteFirstValid(){
		return voteFirstValidIsSet;
	}

	/**
	 * (voteKD) Number of subkeys in each batch of participation keys. 
	 */
	private long voteKeyDilution;
	private boolean voteKeyDilutionIsSet;
	@JsonProperty("vote-key-dilution")
	public void setVoteKeyDilution(long voteKeyDilution){
		this.voteKeyDilution = voteKeyDilution;
		voteKeyDilutionIsSet = true;
	}
	@JsonProperty("vote-key-dilution")
	public Long getVoteKeyDilution(){
		return voteKeyDilutionIsSet ? voteKeyDilution : null;
	}
	/**
	 * Check if has a value for voteKeyDilution 
	 */	@JsonIgnore
	public boolean hasVoteKeyDilution(){
		return voteKeyDilutionIsSet;
	}

	/**
	 * (voteLst) Last round for which this participation is valid. 
	 */
	private long voteLastValid;
	private boolean voteLastValidIsSet;
	@JsonProperty("vote-last-valid")
	public void setVoteLastValid(long voteLastValid){
		this.voteLastValid = voteLastValid;
		voteLastValidIsSet = true;
	}
	@JsonProperty("vote-last-valid")
	public Long getVoteLastValid(){
		return voteLastValidIsSet ? voteLastValid : null;
	}
	/**
	 * Check if has a value for voteLastValid 
	 */	@JsonIgnore
	public boolean hasVoteLastValid(){
		return voteLastValidIsSet;
	}

	/**
	 * (vote) root participation public key (if any) currently registered for this 
	 * round. 
	 */
	private String voteParticipationKey;
	private boolean voteParticipationKeyIsSet;
	@JsonProperty("vote-participation-key")
	public void setVoteParticipationKey(String voteParticipationKey){
		this.voteParticipationKey = voteParticipationKey;
		voteParticipationKeyIsSet = true;
	}
	@JsonProperty("vote-participation-key")
	public String getVoteParticipationKey(){
		return voteParticipationKeyIsSet ? voteParticipationKey : null;
	}
	/**
	 * Check if has a value for voteParticipationKey 
	 */	@JsonIgnore
	public boolean hasVoteParticipationKey(){
		return voteParticipationKeyIsSet;
	}

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
