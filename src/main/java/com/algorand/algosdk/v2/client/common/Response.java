package com.algorand.algosdk.v2.client.common;

import java.io.IOException;

import com.algorand.algosdk.util.Encoder;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.msgpack.jackson.dataformat.MessagePackFactory;

public class Response<T> {
	private static ObjectMapper jsonMapper;
	private static ObjectMapper msgpMapper;

	static {
		msgpMapper = new ObjectMapper(new MessagePackFactory());
		msgpMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
		msgpMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		msgpMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
		// There's some odd bug in Jackson < 2.8.? where null values are not excluded. See:
		// https://github.com/FasterXML/jackson-databind/issues/1351. So we will
		// also annotate all fields manually
		msgpMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);

		jsonMapper = new ObjectMapper();
		jsonMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
		jsonMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
		jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		jsonMapper.setSerializationInclusion(Include.NON_NULL);
		jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
	}

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

	public String getContentType() {
		return contentType;
	}

	@SuppressWarnings("rawtypes")
	public void setValueType(Class valueType) {
		this.valueType = valueType;
	}
	
	@Override
	public String toString() {
		String jsonStr;
		try {
			jsonStr = jsonMapper.writeValueAsString(this.body());
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
		}
		return jsonStr;
	}

	/** The response object in case of a successful request. */
	@SuppressWarnings("unchecked")
	public T body() {
		if (!this.isSuccessful()) {
			return null;
		}

		try {
		    switch (contentType) {
				case "application/json":
				    return convertJson();
				case "application/messagepack":
				case "application/msgpack":
					return convertMessagePack();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private T convertJson() throws IOException {
		return (T) jsonMapper.readValue(body, valueType);
	}

	private T convertMessagePack() throws IOException {
		byte[] bytes = Encoder.decodeFromBase64(body);
		T resp = (T) msgpMapper.readValue(bytes, valueType);
		Object o = Encoder.decodeFromMsgPack(body, valueType);
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
