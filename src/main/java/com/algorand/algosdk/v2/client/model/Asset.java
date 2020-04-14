package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Specifies both the unique identifier and the parameters for an asset 
 */
public class Asset extends PathResponse {

	/**
	 * unique asset identifier 
	 */
	private long index;
	private boolean indexIsSet;
	@JsonProperty("index")
	public void setIndex(long index){
		this.index = index;
		indexIsSet = true;
	}
	@JsonProperty("index")
	public Long getIndex(){
		return indexIsSet ? index : null;
	}
	/**
	 * Check if has a value for index 
	 */	@JsonIgnore
	public boolean hasIndex(){
		return indexIsSet;
	}

	private AssetParams params;
	private boolean paramsIsSet;
	@JsonProperty("params")
	public void setParams(AssetParams params){
		this.params = params;
		paramsIsSet = true;
	}
	@JsonProperty("params")
	public AssetParams getParams(){
		return paramsIsSet ? params : null;
	}
	/**
	 * Check if has a value for params 
	 */	@JsonIgnore
	public boolean hasParams(){
		return paramsIsSet;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		Asset other = (Asset) o;
		if (!Objects.deepEquals(this.index, other.index)) return false;
		if (!Objects.deepEquals(this.params, other.params)) return false;

		return true;
	}
}
