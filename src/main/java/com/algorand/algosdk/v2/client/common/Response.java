package com.algorand.algosdk.v2.client.common;

import java.io.IOException;
import java.util.Map;

import com.algorand.algosdk.util.Encoder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Response<T> {

	private int code;
	private String failureMessage;
	private String body;
	private String contentType;

	@SuppressWarnings("rawtypes")
	private Class valueType;
	
	public Response(int code, String failureMessage, String contentType, String body) {
		this.code = code;
		this.failureMessage = failureMessage;
		this.body = body;
		this.contentType = contentType;
	}
	
	@SuppressWarnings("rawtypes")
	public void setValueType(Class valueType) {
		this.valueType = valueType;
	}
	
	@Override
	public String toString() {
		ObjectMapper om = new ObjectMapper(); 
		String jsonStr;
		try {
			jsonStr = om.setSerializationInclusion(Include.NON_NULL).writeValueAsString(this.body());
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
			/*
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());

			 */
		}
		return jsonStr;
	}

	/** The response object in case of a successful request. */
	@SuppressWarnings("unchecked")
	public T body() {
		if (!this.isSuccessful()) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		T resp;
		try {
		    // Assume JSON response.
			resp = (T) mapper.readValue(body, valueType);
		} catch (IOException e) {
		    try {
		    	// If that doesn't work give message pack a shot.
				resp = (T) Encoder.decodeFromMsgPack(body, valueType);
			} catch (IOException ioException) {
				ioException.printStackTrace();
				return null;
			}
		}
		return resp;
	}

	/** Returns the status message. Describes the failure cause.  */
	public String message() {
		return failureMessage;
	}
	
	public boolean isSuccessful() {
		return code >= 200 && code < 300;
	}

	public int code() {
		return code;
	}
}
