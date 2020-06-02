package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PostTransactionsResponse extends PathResponse {

	/**
	 * encoding of the transaction hash. 
	 */
	@JsonProperty("txId")
	public String txId;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		PostTransactionsResponse other = (PostTransactionsResponse) o;
		if (!Objects.deepEquals(this.txId, other.txId)) return false;

		return true;
	}
}
