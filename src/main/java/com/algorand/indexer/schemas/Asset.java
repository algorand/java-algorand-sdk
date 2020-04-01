package com.algorand.indexer.schemas;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	unique asset identifier 
 */
public class Asset {

	@JsonProperty("params")
	public AssetParams params;

	/*
		unique asset identifier 
	 */
	@JsonProperty("index")
	public long index;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		Asset other = (Asset) o;
		if (!Objects.deepEquals(this.params, other.params)) return false;
		if (!Objects.deepEquals(this.index, other.index)) return false;

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
