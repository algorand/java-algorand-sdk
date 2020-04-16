package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorModel extends PathResponse {

	@JsonProperty("data")
	public String data;

	@JsonProperty("msg")
	public String msg;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		ErrorModel other = (ErrorModel) o;
		if (!Objects.deepEquals(this.data, other.data)) return false;
		if (!Objects.deepEquals(this.msg, other.msg)) return false;

		return true;
	}
}
