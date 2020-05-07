package com.algorand.algosdk.v2.client.common;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class PathResponse {
	
	@Override
	public String toString() {
		try {
			return new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
