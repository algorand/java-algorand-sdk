package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BlockResponse extends PathResponse {

	/**
	 * Block header data. 
	 */
	private String block;
	private boolean blockIsSet;
	@JsonProperty("block")
	public void setBlock(String block){
		this.block = block;
		blockIsSet = true;
	}
	@JsonProperty("block")
	public String getBlock(){
		return blockIsSet ? block : null;
	}
	/**
	 * Check if has a value for block 
	 */	@JsonIgnore
	public boolean hasBlock(){
		return blockIsSet;
	}

	/**
	 * Optional certificate object. This is only included when the format is set to 
	 * message pack. 
	 */
	private String cert;
	private boolean certIsSet;
	@JsonProperty("cert")
	public void setCert(String cert){
		this.cert = cert;
		certIsSet = true;
	}
	@JsonProperty("cert")
	public String getCert(){
		return certIsSet ? cert : null;
	}
	/**
	 * Check if has a value for cert 
	 */	@JsonIgnore
	public boolean hasCert(){
		return certIsSet;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		BlockResponse other = (BlockResponse) o;
		if (!Objects.deepEquals(this.block, other.block)) return false;
		if (!Objects.deepEquals(this.cert, other.cert)) return false;

		return true;
	}
}
