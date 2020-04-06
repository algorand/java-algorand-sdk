package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ErrorModel {

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
