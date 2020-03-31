package com.algorand.indexer.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountParticipation {

	@JsonProperty("vote-first-valid")
	public long voteFirstValid;

	@JsonProperty("vote-last-valid")
	public long voteLastValid;

	@JsonProperty("vote-participation-key")
	public String voteParticipationKey;

	@JsonProperty("vote-key-dilution")
	public long voteKeyDilution;

	@JsonProperty("selection-participation-key")
	public String selectionParticipationKey;
}
