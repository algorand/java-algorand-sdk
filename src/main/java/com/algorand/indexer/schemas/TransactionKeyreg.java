package com.algorand.indexer.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionKeyreg {

	@JsonProperty("vote-participation-key")
	public String voteParticipationKey;

	@JsonProperty("vote-key-dilution")
	public long voteKeyDilution;

	@JsonProperty("selection-participation-key")
	public String selectionParticipationKey;

	@JsonProperty("vote-first-valid")
	public long voteFirstValid;

	@JsonProperty("non-participation")
	public boolean nonParticipation;

	@JsonProperty("vote-last-valid")
	public long voteLastValid;
}
